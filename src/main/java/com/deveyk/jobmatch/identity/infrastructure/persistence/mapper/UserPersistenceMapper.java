package com.deveyk.jobmatch.identity.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.identity.domain.model.JmUser;
import com.deveyk.jobmatch.identity.infrastructure.persistence.entity.JmUserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    // JmUserEntity -> JmUser
    JmUser toDomain(JmUserEntity entity);

    // JmUser -> JmUserEntity
    JmUserEntity toEntity(JmUser domain);

    // JmUserEntity.LoginAttemptEntity -> JmUser.LoginAttempt
    JmUser.LoginAttempt toDomain(JmUserEntity.LoginAttemptEntity entity);

    // JmUser.LoginAttempt -> JmUserEntity.LoginAttemptEntity
    JmUserEntity.LoginAttemptEntity toEntity(JmUser.LoginAttempt domain);

    @AfterMapping
    default void linkBackReferences(@MappingTarget JmUserEntity entity) {

        if (entity.getLoginAttempt() != null) {
            entity.getLoginAttempt().setUser(entity);
        }

    }

}
