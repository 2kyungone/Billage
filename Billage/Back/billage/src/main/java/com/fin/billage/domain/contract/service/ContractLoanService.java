package com.fin.billage.domain.contract.service;

import com.fin.billage.domain.contract.dto.ContractLoanDetailResponseDto;
import com.fin.billage.domain.contract.dto.ContractLoanResponseDto;

import com.fin.billage.domain.contract.entity.Contract;
import com.fin.billage.domain.contract.repository.ContractRepository;
import com.fin.billage.domain.contract.repository.TransactionRepository;
import com.fin.billage.domain.user.entity.User;
import com.fin.billage.domain.user.repository.UserRepository;
import com.fin.billage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ContractLoanService {
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    // 거래내역 계산
    public BigDecimal calculateTransaction(List<BigDecimal> transaction, BigDecimal amount) {
        BigDecimal sum = BigDecimal.ZERO;

        for (BigDecimal t : transaction) {
            sum = sum.add(t); // 현재 합계에 t를 더함
        }

        // amount(빌린금액) - sum
        sum = amount.subtract(sum);

        return sum;
    }

    // 빌려준 거래 목록 리스트
    public List<ContractLoanResponseDto> searchLendList(Long contractId, HttpServletRequest request) {
        Long user_pk = jwtUtil.extractUserPkFromToken(request);
        User user = userRepository.findById(user_pk).orElse(null);

        Contract contract = contractRepository.findByContractId(contractId);

        // 송금인(tran_wd)가 debeter_user인 경우의 tran_amt를 가져와서
        // calculateTransaction(List<Bigdecimal> tran_amt, 빌린금액)에 넣어주기
        String tranWd = contract.getDebtorUser().getUserName();
        List<BigDecimal> tranAmtList = transactionRepository.findTranAmtByContractAndTranWd(contract, tranWd);

        List<Contract> contracts = contractRepository.findAllByCreditorUser(user);

        List<ContractLoanResponseDto> lendList = new ArrayList<>();

        for (Contract c : contracts) {
            ContractLoanResponseDto contractLoanResponseDto = ContractLoanResponseDto.builder()
                    .contractId(c.getContractId())
                    .contractAmt(c.getContractAmt())
                    .contractState(c.getContractState())
                    .creditorUser(c.getCreditorUser())
                    .debtorUser(c.getDebtorUser())
                    .repaymentCash(calculateTransaction(tranAmtList, c.getContractAmt()))
                    .build();

            lendList.add(contractLoanResponseDto);
        }

        return lendList;
    }

    // 빌린 거래 목록 리스트
    public List<ContractLoanResponseDto> searchBorrowList(Long contractId, HttpServletRequest request) {
        Long user_pk = jwtUtil.extractUserPkFromToken(request);
        User user = userRepository.findById(user_pk).orElse(null);

        Contract contract = contractRepository.findByContractId(contractId);

        // 송금인(tran_wd)가 debeter_user인 경우의 tran_amt를 가져와서
        // calculateTransaction(List<Bigdecimal> tran_amt, 빌린금액)에 넣어주기
        String tranWd = contract.getDebtorUser().getUserName();
        List<BigDecimal> tranAmtList = transactionRepository.findTranAmtByContractAndTranWd(contract, tranWd);

        List<Contract> contracts = contractRepository.findAllByDebtorUser(user);

        List<ContractLoanResponseDto> borrowList = new ArrayList<>();

        for (Contract c : contracts) {
            ContractLoanResponseDto contractLoanResponseDto = ContractLoanResponseDto.builder()
                    .contractId(c.getContractId())
                    .contractAmt(c.getContractAmt())
                    .contractState(c.getContractState())
                    .creditorUser(c.getCreditorUser())
                    .debtorUser(c.getDebtorUser())
                    .repaymentCash(calculateTransaction(tranAmtList, c.getContractAmt()))
                    .build();

            borrowList.add(contractLoanResponseDto);
        }

        return borrowList;
    }

    // 거래 상세
    public ContractLoanDetailResponseDto detailLoan(Long contractId, HttpServletRequest request) {
        Contract contract = contractRepository.findByContractId(contractId);
        // 송금인(tran_wd)가 debeter_user인 경우의 tran_amt를 가져와서
        // calculateTransaction(List<Bigdecimal> tran_amt, 빌린금액)에 넣어주기
        String tranWd = contract.getDebtorUser().getUserName();
        List<BigDecimal> tranAmtList = transactionRepository.findTranAmtByContractAndTranWd(contract, tranWd);

        ContractLoanDetailResponseDto contractLoanDetailResponseDto = ContractLoanDetailResponseDto.builder()
                .contractAmt(contract.getContractAmt())
                .contractStartDate(contract.getContractStartDate())
                .contractMaturityDate(contract.getContractMaturityDate())
                .contractInterestRate(contract.getContractInterestRate())
                .repaymentCash(calculateTransaction(tranAmtList, contract.getContractAmt()))
                .build();

        return contractLoanDetailResponseDto;
    }
}
