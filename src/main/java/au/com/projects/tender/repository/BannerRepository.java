package au.com.projects.tender.repository;

import au.com.projects.tender.model.Banner;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BannerRepository extends CrudRepository<Banner, Integer> {

    List<Banner> findByIsMobileOrderByImgOrderAsc(Boolean isMobile);

    List<Banner> findAllByOrderByImgOrderAsc();

    Banner findFirstByIsMobileOrderByIdDesc(Boolean isMobile);
}
