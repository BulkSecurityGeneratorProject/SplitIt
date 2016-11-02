package pl.put.splitit.repository;

import pl.put.splitit.domain.UserGroup;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the UserGroup entity.
 */
@SuppressWarnings("unused")
public interface UserGroupRepository extends JpaRepository<UserGroup,Long> {

    @Query("select userGroup from UserGroup userGroup where userGroup.owner.login = ?#{principal.username}")
    List<UserGroup> findByOwnerIsCurrentUser();

    @Query("select distinct userGroup from UserGroup userGroup left join fetch userGroup.users")
    List<UserGroup> findAllWithEagerRelationships();

    @Query("select userGroup from UserGroup userGroup left join fetch userGroup.users where userGroup.id =:id")
    UserGroup findOneWithEagerRelationships(@Param("id") Long id);

}
