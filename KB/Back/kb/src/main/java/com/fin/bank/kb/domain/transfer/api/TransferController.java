package com.fin.bank.kb.domain.transfer.api;

import com.fin.bank.kb.domain.transfer.dto.TransferRequestDto;
import com.fin.bank.kb.domain.transfer.dto.TransferResponseDto;
import com.fin.bank.kb.domain.transfer.enums.TransactionType;
import com.fin.bank.kb.domain.transfer.service.TransferService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kb/transfer")
public class TransferController {

    private final TransferService transferService;

    /**
     * 빌리지가 오픈뱅킹센터로 요청하면 오픈뱅킹센터가 각 은행에게 입금 또는 출금을 요청한다.
     * 이때 요청하기 위해 각 은행의 API를 오픈뱅킹센터가 호출해서 사용한다.
     * 따라서 이곳에 있는 입금 요청 API, 출금 요청 API는 단순히 해당 은행 내의 고객의 계좌 잔액을 올리고 내리는 것이다.
     */

    // 고객의 입금 요청 API
    @PostMapping("/deposit")
    public ResponseEntity<TransferResponseDto> deposit(@RequestBody TransferRequestDto requestDto) {
        boolean success = transferService.deposit(requestDto);
        System.out.println("들어와라...");
        if (success) {
            return new ResponseEntity<>(createSuccessResponse(TransactionType.DEPOSIT), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(createErrorResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    // 고객의 출금 요청 API
    @PostMapping("/withdraw")
    public ResponseEntity<TransferResponseDto> withdraw(@RequestBody TransferRequestDto requestDto) {
        boolean success = transferService.withdraw(requestDto);
        System.out.println("withdraw확인");
        if (success) {
            return new ResponseEntity<>(createSuccessResponse(TransactionType.WITHDRAWAL), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(createErrorResponse(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/test")
    public void test(){
        System.out.println("test확인");
    }
    

    // 성공 응답 생성 메서드
    private TransferResponseDto createSuccessResponse(TransactionType transactionType) {
        return TransferResponseDto.builder()
                .message(transactionType.getValue() + " successful")
                .build();
    }

    // 에러 응답 생성 메서드
    private TransferResponseDto createErrorResponse() {
        return TransferResponseDto.builder()
                .message("Transaction failed. Check customer information, account number, and balance.")
                .build();
    }
}
