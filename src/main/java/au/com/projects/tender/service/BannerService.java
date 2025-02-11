package au.com.projects.tender.service;

import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.model.Banner;
import au.com.projects.tender.repository.BannerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BannerService {

    private final BannerRepository bannerRepository;
    //path Tests: /xampp/htdocs/assets/img/CarouselImages/
    //path Server: /var/www/html/assets/img/CarouselImages/
    private final String bannerPath = "/var/www/html/assets/img/CarouselImages/";
    private final String bannerFileDefaultName = "banner-sellmycar";
    private final String bannerMobileFileDefaultName = "banner-sellmycar-responsive-";
    private String errorMsg;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<String> getBannerImagesByDeviceType(Boolean isMobile) throws DocumentNotFoundException {
        try {
            log.info("Getting banner images for mobile device: " + isMobile);
            List<String> bannerImagesList = new ArrayList<>();
            for (Banner bannerImg : this.bannerRepository.findByIsMobileOrderByImgOrderAsc(isMobile)) {
                bannerImagesList.add(bannerImg.getImgName());
            }

            if (bannerImagesList.isEmpty()) handleEmptyBannerImages();

            return bannerImagesList;
        } catch (Exception e) {
            this.errorMsg = this.errorMsg == null ? "Error getting banner images for mobile device: " + isMobile : this.errorMsg;
            log.error(errorMsg);
            throw new DocumentNotFoundException(errorMsg);
        }
    }

    public List<Banner> getAllBannerRecords() throws DocumentNotFoundException {
        try {
            List<Banner> bannerList = this.bannerRepository.findAllByOrderByImgOrderAsc();
            if (bannerList.isEmpty()) handleEmptyBannerRecords();

            return bannerList;

        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error getting banner records!" : this.errorMsg;
            log.error(errorMsg);
            throw new DocumentNotFoundException(errorMsg);
        }
    }

    public void changeCarouselImageOrder(int id, int newOrder) throws Exception {
        try {
            log.info("Updating banner image order");
            Optional<Banner> bannerOptional = this.bannerRepository.findById(id);
            if (bannerOptional.isEmpty()) handleNullBannerRecord();
            assert bannerOptional.isPresent();

            Banner banner = bannerOptional.get();
            banner.setImgOrder(newOrder);
            this.bannerRepository.save(banner);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error updating carousel image order!" : this.errorMsg;
            log.error(errorMsg);
            throw new Exception(errorMsg);
        }
    }

    public void deleteCarouselImage(int id) throws Exception {
        try {
            log.info("Deleting banner image");
            Optional<Banner> bannerOptional = this.bannerRepository.findById(id);
            if (bannerOptional.isEmpty()) handleNullBannerRecord();
            assert bannerOptional.isPresent();

            Banner banner = bannerOptional.get();
            File imgToDelete = new File(this.bannerPath + banner.getImgName());
            if (imgToDelete.exists()) {
                boolean deleted = imgToDelete.delete();
                if (!deleted) {
                    log.error("Error deleting image");
                    throw new Exception();
                }
            } else {
                log.error("Error the image does not exist");
                throw new Exception();
            }
            this.bannerRepository.delete(banner);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error deleting carousel image!" : this.errorMsg;
            log.error(errorMsg);
            throw new Exception(errorMsg);
        }
    }

    public void addImageToBanner(Boolean isMobile, Integer imgOrder, MultipartFile img) throws InsertException {
        try {
            log.info("Adding image to banner");

            String imgNewFileName = "";
            String lastFileNumberRecord = "1";
            String originalFilename = img.getOriginalFilename();
            assert originalFilename != null;
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            Banner banner = this.bannerRepository.findFirstByIsMobileOrderByIdDesc(isMobile);
            imgNewFileName = isMobile ? this.bannerMobileFileDefaultName : this.bannerFileDefaultName;
            if (banner != null) {
                int index = banner.getImgName().lastIndexOf(isMobile ? this.bannerMobileFileDefaultName : this.bannerFileDefaultName);
                lastFileNumberRecord = banner.getImgName().substring(isMobile ? this.bannerMobileFileDefaultName.length() + index : this.bannerFileDefaultName.length() + index, banner.getImgName().lastIndexOf("."));
                int lastFileNumber = Integer.parseInt(lastFileNumberRecord);
                lastFileNumber++;
                lastFileNumberRecord = Integer.toString(lastFileNumber);
            }
            imgNewFileName += lastFileNumberRecord + extension;
            Banner bannerToAdd = new Banner();
            bannerToAdd.setImgOrder(imgOrder);
            bannerToAdd.setIsMobile(isMobile);
            bannerToAdd.setImgName(imgNewFileName);
            this.bannerRepository.save(bannerToAdd);
            this.saveFile(img, this.bannerPath + imgNewFileName);

        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error adding new banner image!" : this.errorMsg;
            log.error(errorMsg + " " + ex);
            throw new InsertException(Banner.class.getSimpleName(), errorMsg);
        }
    }

    private void saveFile(MultipartFile file, String filePath) throws IOException {
        byte[] bytes = file.getBytes();
        File newFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(bytes);
        fos.close();
    }

    private void handleEmptyBannerImages() {
        errorMsg = "Error, there are no images saved in the banner.";
        log.error(errorMsg);

        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNullBannerRecord() {
        errorMsg = "Error, banner record doesn't exist.";
        log.error(errorMsg);
        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleEmptyBannerRecords() {
        errorMsg = "Error, there are no banner records.";
        log.error(errorMsg);

        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

