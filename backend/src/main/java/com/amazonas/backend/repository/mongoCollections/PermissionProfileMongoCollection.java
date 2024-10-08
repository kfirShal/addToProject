package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionProfileMongoCollection extends MongoCollection<PermissionsProfile> {
}
