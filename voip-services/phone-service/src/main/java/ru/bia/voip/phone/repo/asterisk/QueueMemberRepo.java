package ru.bia.voip.phone.repo.asterisk;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bia.voip.phone.model.asterisk.QueueMember;

/*@Repository
@Transactional("asteriskTransactionManager")*/
public interface QueueMemberRepo extends PagingAndSortingRepository<QueueMember, Integer> {

}
