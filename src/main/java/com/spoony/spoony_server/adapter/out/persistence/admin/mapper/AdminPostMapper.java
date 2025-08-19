package com.spoony.spoony_server.adapter.out.persistence.admin.mapper;

import com.spoony.spoony_server.adapter.dto.admin.response.AdminPostResponseDTO;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.report.Report;
import com.spoony.spoony_server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminPostMapper {

    private final PostPort postPort;

    public AdminPostResponseDTO toDto(Post post, List<Report> reports) {
        User author = post.getUser();
        Place place = post.getPlace();

        List<String> imageUrls = postPort.findPhotoById(post.getPostId()).stream()
                .map(Photo::getPhotoUrl)
                .toList();

        List<AdminPostResponseDTO.MenuDTO> menuDTOs = postPort.findMenuById(post.getPostId()).stream()
                .map(menu -> AdminPostResponseDTO.MenuDTO.of(
                        String.valueOf(menu.getMenuId()), menu.getMenuName()))
                .toList();

        int reportCount = reports.size();
        boolean isReported = reportCount > 0;

        List<AdminPostResponseDTO.ReportDTO> reportDTOs = isReported
                ? reports.stream().map(report -> AdminPostResponseDTO.ReportDTO.of(
                String.valueOf(report.getReportId()),
                report.getReportType().name(),
                report.getReportDetail(),
                report.getUser().getUserName(),
                report.getCreatedAt()
        )).toList()
                : null;

        return AdminPostResponseDTO.of(
                String.valueOf(post.getPostId()),
                String.valueOf(author.getUserId()),
                author.getUserName(),
                post.getDescription(),
                place.getPlaceName(),
                post.getCons(),
                imageUrls,
                place.getPlaceAddress(),
                menuDTOs,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                isReported,
                reportCount,
                reportDTOs,
                post.getDeletedAt()
        );
    }

    public List<AdminPostResponseDTO> mapPostsToDtos(List<Post> posts, Map<Long, List<Report>> reportsByPostId) {
        return posts.stream()
                .map(post -> toDto(post, reportsByPostId.getOrDefault(post.getPostId(), List.of())))
                .toList();
    }
}
