package edu.eci.arsw.exams.moneylaunderingapi;


import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(value = "/fraud-bank-accounts")
public class MoneyLaunderingController
{
    @Autowired
    @Qualifier("MoneyLaunderingServiceStub")
    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> consultarTodosLosPlanos() {
        return new ResponseEntity<>(moneyLaunderingService.getSuspectAccounts(), HttpStatus.ACCEPTED);
    }

        @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addSuspectAccounts(@RequestBody SuspectAccount account) throws MoneyLaunderingException {
            moneyLaunderingService.addSuspectAccounts(account);
            return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @RequestMapping(value="/{accountId}",method = RequestMethod.GET)
    public ResponseEntity<?> getAccountByID(@PathVariable String accountId) throws MoneyLaunderingException {
        return new ResponseEntity<>(moneyLaunderingService.getAccountStatus(accountId), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/{accountId}", method = RequestMethod.PUT)
    public ResponseEntity<?> actualizarCuenta(@RequestBody SuspectAccount cuenta) throws MoneyLaunderingException {

            moneyLaunderingService.updateAccountStatus(cuenta);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
