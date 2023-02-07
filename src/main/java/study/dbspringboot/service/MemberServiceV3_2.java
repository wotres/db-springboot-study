package study.dbspringboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import study.dbspringboot.domain.Member;
import study.dbspringboot.repository.MemberRepositoryV3;

import java.sql.SQLException;

/**
 * 트랜젝션 - 트랜잭션 템플릿
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_2 {
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(
            PlatformTransactionManager transactionManager,
            MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {
        // 트랜잭션 시작
        txTemplate.executeWithoutResult((status) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void bizLogic(String fromId, String toId, int
            money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생"); }
    }
}
