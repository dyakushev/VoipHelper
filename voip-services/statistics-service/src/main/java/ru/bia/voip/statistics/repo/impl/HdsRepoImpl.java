package ru.bia.voip.statistics.repo.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.bia.voip.statistics.model.ucce.hds.AgentLogout;
import ru.bia.voip.statistics.repo.HdsRepo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class HdsRepoImpl implements HdsRepo {
    @Value("${ucce.hds.agent_logout.jabber_extension_pattern}")
    private String jabberExtensionPattern;

    private SessionFactory sessionFactory;

    public HdsRepoImpl(@Qualifier("hdsSessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<String> listDistinctLoggedExtensionsBetweenDates(Timestamp from, Timestamp to) {
        Session session = sessionFactory.openSession();
        session.setDefaultReadOnly(true);
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<AgentLogout> root = cq.from(AgentLogout.class);

            Predicate betweenDates = cb.between(root.get("logoutDateTime"), from, to);
            Predicate likeJabberExtension = cb.like(root.get("extension"), jabberExtensionPattern);
            Predicate betweenDatesAndLikeJabberExtension = cb.and(betweenDates, likeJabberExtension);

            cq.select(root.get("extension")).distinct(true);
            cq.where(betweenDatesAndLikeJabberExtension);

            Query<String> query = session.createQuery(cq);
            List<String> extensionList = query.getResultList();
            transaction.commit();
            return extensionList;
        } catch (
                Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

    }
}
