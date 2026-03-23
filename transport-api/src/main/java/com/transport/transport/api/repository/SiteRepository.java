package com.transport.transport.api.repository;

import com.transport.transport.api.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    List<Site> findByActifTrue();
}
