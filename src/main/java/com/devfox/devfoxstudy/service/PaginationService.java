package com.devfox.devfoxstudy.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;
    // 状態値で存在具現

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) { // 現在PageNumber, total
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0); //　-が出ないようにMathを使う
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber, endNumber).boxed().toList();
        // PrimitiveInt配列をBoxingしてListでReturn
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    } // 現在このせserviceが知ってるvar lengthを望むと照会できるように開く

}
