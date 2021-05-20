package ru.bia.voip.phone.repo.asterisk.jdbc.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.bia.voip.phone.model.asterisk.AsteriskExtension;
import ru.bia.voip.phone.repo.asterisk.jdbc.AsteriskJdbcExtensionRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
//for education purposes
/*@Repository
@Transactional("asteriskTransactionManager")*/
@Log4j2
public class AsteriskJdbcExtensionRepoImpl implements AsteriskJdbcExtensionRepo {
    private RowMapper<AsteriskExtension> asteriskExtensionRowMapper = new AsteriskRowMapper();
    private JdbcTemplate asteriskJdbcTemplate;

    public AsteriskJdbcExtensionRepoImpl(@Qualifier("asteriskJdbcTemplate") JdbcTemplate asteriskJdbcTemplate) {
        this.asteriskJdbcTemplate = asteriskJdbcTemplate;
    }

    @Override
    public Optional<AsteriskExtension> findById(Integer id) {
        String sql = "select * from extensions where id= ?";
        AsteriskExtension extension = null;
        try {
            extension = asteriskJdbcTemplate.queryForObject(sql, asteriskExtensionRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return Optional.of(extension);
    }

    @Override
    public Iterable<AsteriskExtension> findAllById(List<Integer> ids) {
        Integer id = 0;
        if (!ids.isEmpty())
            id = ids.get(0);
        String sql = "select * from extensions where id= ?";
        return new ArrayList<>(asteriskJdbcTemplate.query(sql, asteriskExtensionRowMapper, id));
    }

    @Override
    public Page<AsteriskExtension> findAsteriskExtensionsByExten(String exten, Pageable pageable) {
        String sql = "select * from extensions where exten = ?";
        List<AsteriskExtension> extensionList = new ArrayList<>(asteriskJdbcTemplate.query(sql, asteriskExtensionRowMapper, exten));
        log.debug(extensionList);
        if (!extensionList.isEmpty())
            return new PageImpl<>(extensionList, pageable, extensionList.size());
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public int save(AsteriskExtension extension) {
        String sql = "update extensions set context=?, priority=?, app=?, appdata=?, exten=? where id = ?";
        Object[] params = {extension.getContext(), extension.getPriority(), extension.getApp(), extension.getAppdata(), extension.getExten(), extension.getId()};
        return asteriskJdbcTemplate.update(sql, params);
    }

    @Override
    public Page<AsteriskExtension> findAll(Pageable pageable) {
        String sql = "select * from extensions";
        List<AsteriskExtension> extensionList = new ArrayList<>(asteriskJdbcTemplate.query(sql, asteriskExtensionRowMapper));
        log.debug(extensionList);
        if (!extensionList.isEmpty())
            return new PageImpl<>(extensionList, pageable, extensionList.size());
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    @Override
    public int updateExtensionIdByExten(Integer id, String exten) {
        String sql = "update extensions set id= ? where exten = ?";
        return asteriskJdbcTemplate.update(sql, id, exten);
    }

    private class AsteriskRowMapper implements RowMapper<AsteriskExtension> {
        @Override
        public AsteriskExtension mapRow(ResultSet resultSet, int i) throws SQLException {
            try {
                int id = resultSet.getInt("id");
                String context = resultSet.getString("context");
                short priority = resultSet.getShort("priority");
                String app = resultSet.getString("app");
                String appdata = resultSet.getString("appdata");
                String exten = resultSet.getString("exten");
                AsteriskExtension extension = new AsteriskExtension();
                extension.setId(id);
                extension.setContext(context);
                extension.setPriority(priority);
                extension.setApp(app);
                extension.setAppdata(appdata);
                extension.setExten(exten);
                return extension;
            } catch (SQLException e) {
                return null;
            }
        }
    }
}
