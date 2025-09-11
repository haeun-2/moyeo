package com.mo.moyeo.common.util.finance_api;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccountUtil {
    @Value("${ACCOUNT_KRW}")
    private String accountKRW;
    @Value("${ACCOUNT_USD}")
    private String accountUSD;
    @Value("${ACCOUNT_EUR}")
    private String accountEUR;
    @Value("${ACCOUNT_JPY}")
    private String accountJPY;
    @Value("${ACCOUNT_CNY}")
    private String accountCNY;
    @Value("${ACCOUNT_GBP}")
    private String accountGBP;
    @Value("${ACCOUNT_CHF}")
    private String accountCHF;
    @Value("${ACCOUNT_CAD}")
    private String accountCAD;

    Map<CurrencyType, String> accountMap = new HashMap<>();
    @PostConstruct
    public void init(){
        accountMap.put(CurrencyType.KRW, accountKRW);
        accountMap.put(CurrencyType.USD, accountUSD);
        accountMap.put(CurrencyType.EUR, accountEUR);
        accountMap.put(CurrencyType.JPY, accountJPY);
        accountMap.put(CurrencyType.CNY, accountCNY);
        accountMap.put(CurrencyType.GBP, accountGBP);
        accountMap.put(CurrencyType.CHF, accountCHF);
        accountMap.put(CurrencyType.CAD, accountCAD);
    }

    public String getAccountByType(CurrencyType currencyType){
        return accountMap.get(currencyType);
    }
}
