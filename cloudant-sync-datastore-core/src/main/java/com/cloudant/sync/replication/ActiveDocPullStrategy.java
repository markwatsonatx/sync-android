/**
 * Copyright (c) 2013 Cloudant, Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.sync.replication;

import com.cloudant.http.HttpConnectionRequestInterceptor;
import com.cloudant.http.HttpConnectionResponseInterceptor;
import com.cloudant.mazha.AllDocsResult;
import com.cloudant.mazha.ChangesResult;
import com.cloudant.sync.datastore.*;

import java.net.URI;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @api_private
 */
class ActiveDocPullStrategy extends PullStrategy implements ReplicationStrategy, ActiveDocFetcher {

    private ActiveDocFetcher activeDocRevFetcher;
    private Datastore target;

    private static final Logger logger = Logger.getLogger(ActiveDocPullStrategy.class
            .getCanonicalName());

    public ActiveDocPullStrategy(URI source,
                                 Datastore target,
                                 PullFilter filter,
                                 List<HttpConnectionRequestInterceptor> requestInterceptors,
                                 List<HttpConnectionResponseInterceptor> responseInterceptors,
                                 ActiveDocFetcher activeDocRevFetcher) {
        super(source, target, filter, requestInterceptors, responseInterceptors);
        this.target = target;
        this.activeDocRevFetcher = activeDocRevFetcher;
        if (this.activeDocRevFetcher == null) {
            this.activeDocRevFetcher = this;
        }
    }

    @Override
    protected ChangesResultWrapper nextBatch() throws DatastoreException {
        List<ActiveDoc> activeDocumentRevisions = this.activeDocRevFetcher.fetchAllActiveDocs();
        // Delete existing documents that have been deleted from the server
        List<String> allDocIds = this.target.getAllDocumentIds();
        for (String dbDocId : allDocIds) {
            boolean found = false;
            if (activeDocumentRevisions != null) {
                for (ActiveDoc activeDocumentRevision : activeDocumentRevisions) {
                    if (dbDocId.equalsIgnoreCase(activeDocumentRevision.getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (! found) {
                try {
                    logger.log(Level.INFO, String.format("Deleting document with id %s", dbDocId));
                    this.target.deleteDocument(dbDocId);
                }
                catch (DocumentException ex) {
                    logger.log(Level.WARNING, "Error deleting document.", ex);
                }
            }
        }
        // mimic a changes result
        List<ChangesResult.Row> changeRows = new ArrayList<ChangesResult.Row>();
        if (activeDocumentRevisions != null) {
            for (ActiveDoc activeDocumentRevision : activeDocumentRevisions) {
                ChangesResult.Row changeRow = new ChangesResult.Row();
                changeRow.setId(activeDocumentRevision.getId());
                changeRow.setSeq(activeDocumentRevision.getRevision());
                ChangesResult.Row.Rev changeRowRev = new ChangesResult.Row.Rev();
                changeRowRev.setRev(activeDocumentRevision.getRevision());
                changeRow.setChanges(Arrays.asList(changeRowRev));
                changeRows.add(changeRow);
                logger.log(Level.INFO, String.format("Adding %s/%s to changes",changeRow.getId(),changeRowRev.getRev()));
            }
        }
        ChangesResult changeFeeds = new ChangesResult();
        changeFeeds.setResults(changeRows);
        return new ChangesResultWrapper(changeFeeds);
    }

    @Override
    public List<ActiveDoc> fetchAllActiveDocs() {
        AllDocsResult allDocsResult = this.sourceDb.getAllDocs(false);
        logger.log(Level.INFO, String.format("allDocsResults.size = %d",(allDocsResult == null ? 0 : allDocsResult.size())));
        List<ActiveDoc> activeDocumentRevisions = new ArrayList<ActiveDoc>();
        if (allDocsResult != null && allDocsResult.getRows() != null) {
            for (AllDocsResult.Row row : allDocsResult.getRows()) {
                activeDocumentRevisions.add(new ActiveDoc(row.getId(), row.getRev()));
            }
        }
        return activeDocumentRevisions;
    }
}
