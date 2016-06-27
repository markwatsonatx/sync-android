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
import com.cloudant.mazha.CouchClient;
import com.cloudant.mazha.DocumentRevs;
import com.cloudant.sync.datastore.*;
import com.cloudant.sync.event.EventBus;
import com.cloudant.sync.util.JSONUtils;
import com.cloudant.sync.util.Misc;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

// mw:
/**
 * @api_private
 */
class PullStrategy2 extends PullStrategy implements ReplicationStrategy {


    private static final Logger logger = Logger.getLogger(PullStrategy2.class
            .getCanonicalName());

    public PullStrategy2(URI source,
                        Datastore target,
                        PullFilter filter,
                        List<HttpConnectionRequestInterceptor> requestInterceptors,
                        List<HttpConnectionResponseInterceptor> responseInterceptors) {
        super(source, target, filter, requestInterceptors, responseInterceptors);
    }

    @Override
    protected ChangesResultWrapper nextBatch() throws DatastoreException {
        logger.log(Level.INFO, "nextBatch called on PullStrategy2");
        AllDocsResult allDocsResult = this.sourceDb.getAllDocs(false);
        logger.log(Level.INFO, String.format("allDocsResults.size = %d",(allDocsResult == null ? 0 : allDocsResult.size())));
        List<ChangesResult.Row> changeRows = new ArrayList<ChangesResult.Row>();
        if (allDocsResult != null && allDocsResult.getRows() != null) {
            for(AllDocsResult.Row row : allDocsResult.getRows()) {
                ChangesResult.Row changeRow = new ChangesResult.Row();
                changeRow.setId(row.getId());
                changeRow.setSeq(row.getRev());
                ChangesResult.Row.Rev changeRowRev = new ChangesResult.Row.Rev();
                changeRowRev.setRev(row.getRev());
                changeRow.setChanges(Arrays.asList(changeRowRev));
                changeRows.add(changeRow);
                logger.log(Level.INFO, String.format("Adding %s/%s to changes",changeRow.getId(),changeRowRev.getRev()));
            }
        }
        ChangesResult changeFeeds = new ChangesResult();
        changeFeeds.setResults(changeRows);
        return new ChangesResultWrapper(changeFeeds);
    }
}
