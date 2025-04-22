package com.spoony.spoony_server.application.service.user;

import com.spoony.spoony_server.adapter.dto.user.ProfileImageListResponseDTO;
import com.spoony.spoony_server.adapter.dto.user.ProfileImageResponseDTO;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.user.ProfileImageGetUseCase;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileImageService implements ProfileImageGetUseCase {
    private final PostPort postPort;
    private final ZzimPostPort zzimPostPort;


    @Override
    public ProfileImageListResponseDTO getAvailableProfileImages(UserGetCommand command) {
        List<Post> postList = postPort.findPostsByUserId(command.getUserId());


        Long totalZzimCount = postList.stream().mapToLong(post -> zzimPostPort.countZzimByPostId(post.getPostId())).sum();

        List<ProfileImageResponseDTO> unlockedImages = new ArrayList<>();

        for(ProfileImage profileImage : ProfileImage.values()){

            boolean isUnlocked = totalZzimCount >= profileImage.getRequiredZzimCount();


            if (isUnlocked){
                unlockedImages.add(ProfileImageResponseDTO.of(profileImage,true));
            }

            if (totalZzimCount < profileImage.getRequiredZzimCount()) {
                break;
            }
        }
        return new ProfileImageListResponseDTO(unlockedImages);

    }
}
