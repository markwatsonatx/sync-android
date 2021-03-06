/*
 * Copyright (c) 2016 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.cloudant.sync.datastore;

import java.util.List;
import java.util.Map;

/**
 * Created by tomblench on 22/01/16.
 *
 * @api_private
 */
public class ForceInsertItem {
    public DocumentRevision rev;
    public List<String> revisionHistory;
    public Map<String, Object> attachments;
    public Map<String[],List<PreparedAttachment>>preparedAttachments;
    public boolean pullAttachmentsInline;

    public ForceInsertItem(DocumentRevision rev, List<String> revisionHistory,
                           Map<String, Object> attachments, Map<String[],
            List<PreparedAttachment>> preparedAttachments, boolean pullAttachmentsInline) {
        this.rev = rev;
        this.revisionHistory = revisionHistory;
        this.attachments = attachments;
        this.preparedAttachments = preparedAttachments;
        this.pullAttachmentsInline = pullAttachmentsInline;
    }
}
