package lab.contract.findout.certifiedcopy_content.service;

import lab.contract.allcertified.certifiedcopy.persistence.Certifiedcopy;
import lab.contract.allcertified.certifiedcopy.persistence.CertifiedcopyRepository;
import lab.contract.findout.certifiedcopy_content.persistence.CertifiedcopyContent;
import lab.contract.findout.certifiedcopy_content.persistence.CertifiedcopyContentRepository;
import lab.contract.findout.contract_content.persistence.ContractContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class CertifiedcopyContentService {
    private final CertifiedcopyContentRepository certifiedcopyContentRepository;
    private final CertifiedcopyRepository certifiedcopyRepository;

    public Long saveCertifiedcopyContent(Long certifiedcopyId, ArrayList<String[]> certifiedcopyText) {

        Certifiedcopy certifiedcopy = certifiedcopyRepository.findById(certifiedcopyId).orElseThrow(EntityNotFoundException::new);
        String totalAddress = "";
        String streetAddress = "";
        String registerPurpose = "";
        String ownerName = "";
        String ownerResidentNumber = "";
        String ownerAddress = "";
        Double ownerPart = 0.0;
        String sharerName = "";
        String sharerResidentNumber = "";
        String sharerAddress = "";
        Double sharerPart = 0.0;
        long mortgage=0;

        try {
            for (int i = 0; i < certifiedcopyText.size(); i++) {
                String key = certifiedcopyText.get(i)[0];
                String content = certifiedcopyText.get(i)[1];
                switch (key) {
                    case "전체 지번":
                        if (!totalAddress.isBlank()) break;
                        totalAddress = content;
                        System.out.println("totalAddress = " + totalAddress);
                        break;
                    case "소재지번과 도로명주소":
                        streetAddress = extractStreetAddress(content);
                        System.out.println("streetAddress = " + streetAddress);
                        break;
                    case "등기목적":
                        registerPurpose += content;
                        //System.out.println("registerPurpose = " + registerPurpose);
                        break;
                    case "권리자 및 기타사항":
                        Object[] owner = extractOwner(content);
                        ownerName = (String) owner[0];
                        ownerResidentNumber = (String) owner[1];
                        ownerAddress = (String) owner[2];
                        ownerPart = (Double) owner[3];
                        sharerName = (String) owner[4];
                        sharerResidentNumber = (String) owner[5];
                        sharerAddress = (String) owner[6];
                        sharerPart = (Double) owner[7];
                        mortgage = extractMortgage(content);
                        break;
                }
            }
        } catch(NullPointerException ex){
            throw new IllegalArgumentException("파일을 다시 업로드 해주세요");
        }

        CertifiedcopyContent saveCertifiedcopyContent = CertifiedcopyContent.builder()
                .certifiedcopy(certifiedcopy)
                .total_address(totalAddress)
                .street_address(streetAddress)
                .register_purpose(registerPurpose)
                .owner_name(ownerName)
                .owner_resident_number(ownerResidentNumber)
                .owner_address(ownerAddress)
                .owner_part(ownerPart)
                .sharer_name(sharerName)
                .sharer_resident_number(sharerResidentNumber)
                .sharer_address(sharerAddress)
                .sharer_part(sharerPart)
                .mortgage(mortgage).build();
        certifiedcopy.setCertifiedcopyContent(saveCertifiedcopyContent);
        return certifiedcopyContentRepository.save(saveCertifiedcopyContent).getId();
    }
    private static long extractMortgage(String text) {
        int mortgageIndex = text.indexOf("채권최고액");
        if (mortgageIndex == -1) {
            log.info("채권최고액이 존재하지 않습니다.");
            return -1;
        }
        int endIndex = text.indexOf("원",mortgageIndex);
        text = text.substring(mortgageIndex+7,endIndex).replace(",","");
        System.out.println("채권최고액 = " + text);
        return Long.valueOf(text);
    }

    private static String extractStreetAddress(String text) {
        System.out.println("도로명주소 찾기");
        int streetAddressIndex = text.indexOf("[도로명주소]");
        System.out.println("streetAddressIndex = " + streetAddressIndex);
        if (streetAddressIndex==-1) {
            log.info("도로명주소를 찾을 수 없습니다.");
            return "찾는 단어가 존재하지 않습니다.";
        }

        text = text.replace("[도로명주소]","").substring(streetAddressIndex).replace("\n"," ");
        System.out.println("도로명주소 = " + text);
        String[] s = text.split(" ");
        int separateLine = 0; //로 길 구분 라인 인덱스
        String streetNumber = "";
        String res = "";

        for (int i=0;i<s.length;i++) {
            if (s[i].endsWith("로") || s[i].endsWith("길")) separateLine = i;
        }

        /**
         이건 안필요할수도 있..어 ..
         */
        for (int i=separateLine+2;i<s.length;i++) {
            s[separateLine+1] += s[i];
        }
        for (int i=0;i<s[separateLine+1].length();i++) {
            int current = s[separateLine+1].charAt(i)-'0';
            if (0 <= current && current <= 9) streetNumber+=current; //숫자인지 체크
            else break;
        }
        for (int i=0;i<=separateLine;i++) {
            res += " "+s[i]; //-시,-구,-로 다시 합침
        }
        res += " " + streetNumber; //숫자 붙여줌
        return res.trim();
    }

    private static Object[] extractOwner(String fullText) {
        String temp = "";
        String ownerName= "";
        String ownerResidentNumber= "";
        String ownerAddress= "";
        Double ownerPart = 0.0;
        String sharerName = "";
        String sharerResidentNumber = "";
        String sharerAddress = "";
        Double sharerPart = 0.0;

        int ownerIndex = fullText.lastIndexOf("소유자");
        int sharerIndex = fullText.lastIndexOf("공유자");
        if (ownerIndex > sharerIndex) {
            int endIndex = fullText.indexOf("거래가액",ownerIndex);
            temp = fullText.substring(ownerIndex, endIndex);
            String[] s = temp.split("\n");
            ownerName = s[0].substring(4,s[0].length()-15);
            ownerResidentNumber = s[0].substring(s[0].length()-14);
            System.out.println("ownerName = " + ownerName);
            System.out.println("ownerResidentNumber = " + ownerResidentNumber);
            ownerAddress = "";
            for (int i=1;i<s.length;i++) {
                ownerAddress += s[i];
            }
            System.out.println("ownerAddress = " + ownerAddress);
            ownerPart = 1.0;

        }else {
            if (sharerIndex == -1) return new String[] {"찾는 단어가 존재하지 않습니다."};
            int endIndex = fullText.indexOf("거래가액",ownerIndex);
            temp = fullText.substring(sharerIndex, endIndex);
            String[] s = temp.split("\n");
            String[] part = s[1].replace("지분 ","").split("분의 ");
            ownerPart = Double.parseDouble(part[1])/Double.parseDouble(part[0]);
            ownerName = s[2].substring(0,s[2].length()-15);
            ownerResidentNumber = s[2].substring(s[2].length()-14);
            ownerAddress = "";
            for (int i=3;i<s.length;i++) {
                if (s[i].startsWith("지분")) break;
                ownerAddress += s[i];
            }
            int sharer2Index = temp.lastIndexOf("지분");
            temp = temp.substring(sharer2Index);
            String[] s2 = temp.split("\n");
            String[] part2 = s2[0].replace("지분 ","").split("분의 ");
            sharerPart = Double.parseDouble(part2[1])/Double.parseDouble(part2[0]);
            sharerName = s2[1].substring(0,s2[1].length()-15);
            sharerResidentNumber = s2[1].substring(s2[1].length()-14);
            sharerAddress = "";
            for (int i=2;i<s2.length;i++) {
                sharerAddress += s2[i];
            }
            System.out.println("ownerName = " + ownerName);
            System.out.println("ownerResidentNumber = " + ownerResidentNumber);
            System.out.println("ownerAddress = " + ownerAddress);
            System.out.println("sharerName = " + sharerName);
            System.out.println("sharerResidentNumber = " + sharerResidentNumber);
            System.out.println("sharerAddress = " + sharerAddress);
        }
        return new Object[] {ownerName,ownerResidentNumber,ownerAddress,ownerPart,sharerName,sharerResidentNumber,sharerAddress,sharerPart};
    }

}
