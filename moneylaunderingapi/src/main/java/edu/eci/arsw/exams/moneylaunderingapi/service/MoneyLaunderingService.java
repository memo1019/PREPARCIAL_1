package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.MoneyLaunderingException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.List;

public interface MoneyLaunderingService {
    void updateAccountStatus(String accountId,SuspectAccount suspectAccount)throws MoneyLaunderingException;
    SuspectAccount getAccountStatus(String accountId)throws MoneyLaunderingException;
    List<SuspectAccount> getSuspectAccounts();
    void addAccount(SuspectAccount suspectAccount)throws MoneyLaunderingException;
}