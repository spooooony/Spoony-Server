package com.spoony.spoony_server.domain.user;


import lombok.Getter;

@Getter
public enum ProfileImage {
    LEVEL_1(1, "신생 스푸니안", "기본 스푼. 스푸니에 오신 걸 환영해요!", "profile1.png",0),
    LEVEL_2(2,"탐구자 스푸니오", "내 리뷰가 다른 유저들의 지도에 10번 저장", "profile2.png",10),
    LEVEL_3(3, "학자 스푸니테스","내 리뷰가 다른 유저들의 지도에 30번 저장", "profile3.png",30),
    LEVEL_4(4, "대탐험가 스푸니코스", "내 리뷰가 다른 유저들의 지도에 50번 저장", "profile4.png",50),
    LEVEL_5(5,"선구자 스푸니엘", "내 리뷰가 다른 유저들의 지도에 70번 저장", "profile5.png",70),
    LEVEL_6(6, "대왕 스푸니우스", "내 리뷰가 다른 유저들의 지도에 100번 저장", "profile6.png",100);

    private final int imageLevel;
    private final String spoonName;
    private final String unlockCondition;
    private final String image;
    private final int requiredZzimCount;

    ProfileImage(int imageLevel,String spoonName, String unlockCondition, String image, int requiredZzimCount) {
        this.imageLevel = imageLevel;
        this.spoonName = spoonName;
        this.unlockCondition = unlockCondition;
        this.image = image;
        this.requiredZzimCount = requiredZzimCount;
    }
    // imageLevel에 해당하는 ProfileImage를 찾는 메서드
    public static ProfileImage fromLevel(int imageLevel) {
        for (ProfileImage profileImage : values()) {
            if (profileImage.getImageLevel() == imageLevel) {
                return profileImage;
            }
        }
        throw new IllegalArgumentException("Invalid image level: " + imageLevel);
    } //test
}
