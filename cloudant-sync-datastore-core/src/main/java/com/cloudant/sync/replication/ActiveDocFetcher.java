package com.cloudant.sync.replication;

import java.util.List;

public interface ActiveDocFetcher {
    public List<ActiveDoc> fetchAllActiveDocs();
}
