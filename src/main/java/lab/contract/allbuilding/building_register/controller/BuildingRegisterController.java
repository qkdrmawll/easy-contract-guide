package lab.contract.allbuilding.building_register.controller;

import lab.contract.allbuilding.building_register.service.BuildingRegisterService;
import lab.contract.allbuilding.building_register_img.service.BuildingRegisterImgService;
import lab.contract.infrastructure.exception.DefaultRes;
import lab.contract.infrastructure.exception.ResponseMessage;
import lab.contract.infrastructure.exception.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@CrossOrigin(originPatterns = "*")
@RestController
@RequiredArgsConstructor
public class BuildingRegisterController {
    private final BuildingRegisterService buildingRegisterService;
    private final BuildingRegisterImgService buildingRegisterImgService;

    @PostMapping("/file/building-register")
    public ResponseEntity buildingRegisterUpload(
            BuildingRequestDto buildingRequestDto) throws IOException, ExecutionException, InterruptedException {
        Long saveId = buildingRegisterService.saveBuildingRegister(buildingRequestDto.getContractId());
        String fileName = buildingRegisterService.savePdfFile(buildingRequestDto.getPdfFile());
        buildingRegisterImgService.convertPdfToPng(fileName);
        buildingRegisterImgService.saveBuildingRegisterImg(saveId,fileName);
        return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.SUCCESS), HttpStatus.OK);
    }

}
