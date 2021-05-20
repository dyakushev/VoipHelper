package ru.bia.voip.statistics.repo.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;
import ru.bia.voip.statistics.repo.AsteriskCdrRepo;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class AsteriskCdrRepoImpl implements AsteriskCdrRepo {
    private static final String CALL_DATE_COLUMN = "calldate";
    private static final String SRC_COLUMN = "src";
    private static final String DST_COLUMN = "dst";

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(@Qualifier("asteriskSessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<AsteriskCdr> listCdrsByDateAndCallingNumber(Timestamp from, Timestamp to, String callingNumber) {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<AsteriskCdr> cq = cb.createQuery(AsteriskCdr.class);
            Root<AsteriskCdr> root = cq.from(AsteriskCdr.class);
            Predicate[] predicates = new Predicate[2];
            Predicate betweenDates = cb.between(root.get(CALL_DATE_COLUMN), from, to);

            Predicate numberLike = cb.like(root.get("clid"), "%" + callingNumber + "%");
            predicates[0] = betweenDates;
            predicates[1] = numberLike;
            cq.select(root).where(cb.and(predicates));

            Query<AsteriskCdr> query = session.createQuery(cq);
            List<AsteriskCdr> asteriskCdrs = query.getResultList();

            transaction.commit();
            return asteriskCdrs;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }

    @Override
    public Optional<Long> getCountByDateAndCallingNumber(Timestamp from, Timestamp to, String callingNumber) {

        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.getTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<AsteriskCdr> root = cq.from(AsteriskCdr.class);
            Predicate betweenDates = cb.between(root.get(CALL_DATE_COLUMN), from, to);
            Predicate equalsCallingNumber = cb.equal(root.get(SRC_COLUMN), callingNumber);
            cq.select(cb.count(root));
            cq.where(cb.and(betweenDates, equalsCallingNumber));

            Query<Long> query = session.createQuery(cq);
            Long count = query.getSingleResult();

            transaction.commit();
            return Optional.of(count);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Long> getCountByDateAndCallingNumberAndCalledNumber(Timestamp from, Timestamp to, String number) {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<AsteriskCdr> root = cq.from(AsteriskCdr.class);


            Predicate betweenDates = cb.between(root.get(CALL_DATE_COLUMN), from, to);
            Predicate equalsCallingNumber = cb.like(root.get(SRC_COLUMN), number);
            Predicate equalsCalledNumber = cb.equal(root.get(DST_COLUMN), number);
            Predicate numberOr = cb.or(equalsCallingNumber, equalsCalledNumber);
            Predicate betweenDatesAndNumber = cb.and(betweenDates, numberOr);

            cq.select(cb.count(root));
            cq.where(betweenDatesAndNumber);
            Query<Long> query = session.createQuery(cq);
            Long count = query.getSingleResult();

            transaction.commit();
            return Optional.of(count);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }

    @Override
    public List<String> listCallingNumbersByDate(Timestamp from, Timestamp to) {
        return this.listNumbersByDateAndDirection(from, to, SRC_COLUMN);
    }

    @Override
    public List<String> listCalledNumbersByDate(Timestamp from, Timestamp to) {
        return this.listNumbersByDateAndDirection(from, to, DST_COLUMN);
    }


    @Override
    public Map<String, Long> mapExtensionToNumberOfIncomingCallsByDate(Timestamp from, Timestamp to, List<String> dstList) {
        //  String sql = "select dst as dstAlias, count(dst) as dstCount from AsteriskCdr where length(dst)=5 and (dst like '6%' or dst like '7%') and calldate between " +from +" and "+ to +" group by dst";
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Map<String, Long> extensionsCountMap = session
                    .createNamedQuery("mapExtensionToNumberOfIncomingCallsByDate", Tuple.class).
                            setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("dstList", dstList)
                    //      .createQuery(sql, Tuple.class)
                    .getResultStream()
                    .collect(Collectors.toMap(
                            ac -> (String) ac.get("dstAlias"),
                            ac -> (Long) ac.get("dstCount")));
            transaction.commit();
            return extensionsCountMap;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Map<String, Long> mapExtensionToNumberOfOutgoingCallsByDate(Timestamp from, Timestamp to, List<String> srcList) {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Map<String, Long> extensionsCountMap = session
                    .createNamedQuery("mapExtensionToNumberOfOutgoingCallsByDate", Tuple.class).
                            setParameter("from", from)
                    .setParameter("to", to)
                    .setParameter("srcList", srcList)
                    //      .createQuery(sql, Tuple.class)
                    .getResultStream()
                    .collect(Collectors.toMap(
                            ac -> (String) ac.get("srcAlias"),
                            ac -> (Long) ac.get("srcCount")));
            transaction.commit();
            return extensionsCountMap;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private List<String> listNumbersByDateAndDirection(Timestamp from, Timestamp to, String direction) {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<AsteriskCdr> root = cq.from(AsteriskCdr.class);
            Predicate betweenDates = cb.between(root.get(CALL_DATE_COLUMN), from, to);

            cq.select(root.get(direction)).distinct(true);
            cq.where(betweenDates);
            Query<String> query = session.createQuery(cq);
            List<String> numbersList = query.getResultList();

            transaction.commit();
            return numbersList;
            //
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


}
