package ec.edu.espe.banco.service;

import ec.edu.espe.banco.api.dto.TransactionDTO;
import ec.edu.espe.banco.entity.AccountEntity;
import ec.edu.espe.banco.entity.TransactionEntity;
import ec.edu.espe.banco.exception.DocumentNotFoundException;
import ec.edu.espe.banco.exception.InsertException;
import ec.edu.espe.banco.repository.AccountRepository;
import ec.edu.espe.banco.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    private String msgError;

    public void create(TransactionDTO transactionDTO) throws InsertException {
        try {
            Optional<AccountEntity> accountOptional = this.accountRepository.findById(transactionDTO.getAccountId());

            if (!accountOptional.isPresent()) {
                this.msgError = "Account doen't exist";
                throw new InsertException(this.msgError, TransactionEntity.class.getName());
            }

            if (transactionDTO.getAmount() <= 0) {
                this.msgError = "Invalid transfer amount";
                throw new InsertException(this.msgError, TransactionEntity.class.getName());
            }

            TransactionEntity transactionToCreate = new TransactionEntity();
            transactionToCreate.setAccount(accountOptional.get());
            transactionToCreate.setType(transactionDTO.getType());
            transactionToCreate.setAmount(transactionDTO.getAmount());
            transactionToCreate.setDescription(transactionDTO.getDescription());
            transactionToCreate.setDate(new Date());
            this.transactionRepository.save(transactionToCreate);
        } catch (Exception exception) {
            this.msgError = this.msgError == null ? "Error creating new transaction" : this.msgError;
            throw new InsertException(this.msgError, TransactionEntity.class.getName());
        }
    }

    public ArrayList<TransactionEntity> getTransactionsByAccountId(Integer id) throws DocumentNotFoundException {
        try {
            ArrayList<TransactionEntity> transactions = this.transactionRepository.findByAccountId(id);

            if (!transactions.isEmpty()) {
                return transactions;
            }
            this.msgError = "No transactions on this account";
            throw new DocumentNotFoundException(this.msgError, TransactionEntity.class.getName());

        } catch (Exception exception) {
            this.msgError = this.msgError == null ? "Error getting account's transactions" : this.msgError;
            throw new DocumentNotFoundException(this.msgError, TransactionEntity.class.getName());
        }
    }
}
