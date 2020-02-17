package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.domain.Sort;
import com.truthbean.debbie.util.StringUtils;

import java.util.Collection;
import java.util.List;

public class BaseRepository<Domain, ID> extends CustomRepository<Domain, ID> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseRepository.class);

    /**
     * Finds all domain by id list and the specified sort.
     *
     * @param ids  id list of domain must not be null
     * @param sort the specified sort must not be null
     * @return a list of domains
     */
    public List<Domain> findAllByIdIn(Collection<ID> ids, Sort sort) {
        String whereSql = "id in (?) order by " + sort.toString();
        return super.findList(whereSql, StringUtils.joining(ids, ","));

    }

    public Page<Domain> findAllByIdIn(Collection<ID> ids, PageRequest pageable) {
        String whereSql = "id in (?)";
        return super.findPaged(pageable, whereSql, StringUtils.joining(ids, ","));
    }

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    @JdbcTransactional(readonly = false)
    public long deleteByIdIn(Iterable<ID> ids) {
        log.debug("Customized deleteByIdIn method was invoked");
        var ref = new Object() {
            long l = 0L;
        };
        ids.forEach(id -> {
            if (super.deleteById(id)) {
                ref.l++;
            }
        });
        return ref.l;
    }

    public List<Domain> findAllById(Collection<ID> ids) {
        String whereSql = "id in (?)";
        return super.findList(whereSql, StringUtils.joining(ids, ","));
    }
}