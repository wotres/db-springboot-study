package study.dbspringboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import study.dbspringboot.domain.Member;
import study.dbspringboot.repository.MemberRepositoryV3;

import java.sql.SQLException;

/**
 * 트랜젝션 - @Transactional AOP
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
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
