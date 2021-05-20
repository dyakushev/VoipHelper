package ru.bia.voip.phone.repo.asterisk;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;

import java.util.List;

@Repository
@Transactional("asteriskTransactionManager")
/*@RepositoryRestResource(collectionResourceRel = "extensions", path = "extensions")*/
public interface AsteriskExtensionRepo extends PagingAndSortingRepository<AsteriskExtension, Integer> {

    @RestResource(exported = false)
    Page<AsteriskExtension> findAsteriskExtensionsByExten(@Param("exten") String exten, Pageable pageable);

    @RestResource(exported = false)
    @Modifying
    @Query("update AsteriskExtension ext set ext.id= :id where ext.exten= :exten")
    int updateExtensionIdByExten(@Param("id") Integer id, @Param("exten") String exten);


    @Override
    @RestResource(exported = false)
    <S extends AsteriskExtension> S save(S s);

    @Override
    @RestResource(exported = false)
    <S extends AsteriskExtension> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    @RestResource(exported = false)
    void deleteById(Integer integer);

    @Override
    @RestResource(exported = false)
    void delete(AsteriskExtension extension);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends AsteriskExtension> iterable);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    List<AsteriskExtension> findAllByIdAndApp(Integer id, String app);
}


//by overriding methods we are disabling all changes

