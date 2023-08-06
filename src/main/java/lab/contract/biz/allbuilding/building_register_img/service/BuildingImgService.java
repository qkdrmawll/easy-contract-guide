package lab.contract.biz.allbuilding.building_register_img.service;

import lab.contract.biz.allbuilding.building_register_img.persistence.entity.ContractImg;
import lab.contract.biz.allbuilding.building_register_img.persistence.repository.ContractImgRepository;
import lab.contract.biz.allcontract.contract.persistence.entity.Contract;
import lab.contract.biz.allcontract.contract.persistence.repository.ContractRepository;
import lab.contract.biz.openapi.convert.ConvertAPI;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@RequiredArgsConstructor
public class ContractImgService {

    private final ContractImgRepository contractImgRepository;
    private final ContractRepository contractRepository;
    private final ConvertAPI convertAPI;
    private static final String UPLOAD_PATH = "C:/contract/getpdf/";
    private static final String DOWNLOAD_PATH = "C:/contract/savepng/";


    public void convertPdfToPng(String pdfFileName) throws IOException, ExecutionException, InterruptedException {
        convertAPI.convertApi(pdfFileName);
    }
    public int savaContractImg(Long contractId, String pdfFileName) throws IOException {

        Optional<Contract> contract = contractRepository.findById(contractId);
        if (!contract.isPresent()) {
            // contract 예외 처리
        }
        File source = new File(UPLOAD_PATH + pdfFileName);
        PDDocument document = PDDocument.load(source);
        int pagesOfPdf = document.getNumberOfPages();

        for (int i = 1; i <= pagesOfPdf; i++) {
            if (i == 1) {
                contractImgRepository.save(ContractImg.builder()
                        .contract(contract.get())
                        .page(i)
                        .url(DOWNLOAD_PATH + pdfFileName).build());
            } else {
                contractImgRepository.save(ContractImg.builder()
                        .contract(contract.get())
                        .page(i)
                        .url(DOWNLOAD_PATH + pdfFileName + "_" + i).build());
            }
        }
        return pagesOfPdf;
    }

}
