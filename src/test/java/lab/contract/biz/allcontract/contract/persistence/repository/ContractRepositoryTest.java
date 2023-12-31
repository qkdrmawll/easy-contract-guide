<<<<<<<< HEAD:src/test/java/lab/contract/biz/allcontract/contract/persistence/repository/ContractRepositoryTest.java
package lab.contract.biz.allcontract.contract.persistence.repository;
========
package lab.contract.allcontract.contract.persistence.repository;
>>>>>>>> master:src/test/java/lab/contract/allcontract/contract/persistence/repository/ContractRepositoryTest.java

import lab.contract.allcontract.contract.persistence.Contract;
import lab.contract.allcontract.contract.persistence.ContractRepository;
import lab.contract.user.persistence.User;
import lab.contract.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ContractRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ContractRepository contractRepository;

    @Test
    public void 계약서_DB연동_테스트() {
        //given
        User user = User.builder()
                .username("홍길동")
                .email("test@test.com")
                .password("1234")
                .privacy_agreement_yn("y")
                .build();
        userRepository.save(user);

        //when
        contractRepository.save(Contract.builder()
                .user(user)
                .contract_name("untitled")
                .build());
        List<Contract> contracts = contractRepository.findAll();

        //then
        Contract contract = contracts.get(0);
        assertThat(contract.getContract_name()).isEqualTo("untitled");

    }
}
