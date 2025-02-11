package ec.edu.espe.banco.service;

import ec.edu.espe.banco.api.dto.TransferDTO;
import ec.edu.espe.banco.entity.AccountEntity;
import ec.edu.espe.banco.entity.TransferEntity;
import ec.edu.espe.banco.exception.DocumentNotFoundException;
import ec.edu.espe.banco.exception.InsertException;
import ec.edu.espe.banco.repository.AccountRepository;
import ec.edu.espe.banco.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class TransferService {

    private final TransferRepository transferRepository;

    private final AccountRepository accountRepository;

    private String msgError;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(TransferDTO transferDTO) throws InsertException {
        try {

            this.validateTransfer(transferDTO);

            AccountEntity sourceAccount = this.accountRepository
                    .findById(transferDTO.getSourceAccountId()).get();
            AccountEntity targetAccount = this.accountRepository
                    .findById(transferDTO.getTargetAccountId()).get();

            //REGISTRO TRANSFERENCIA
            TransferEntity transferToCreate = new TransferEntity();
            transferToCreate.setSourceAccount(sourceAccount);
            transferToCreate.setTargetAccount(targetAccount);
            transferToCreate.setAmount(transferDTO.getAmount());
            transferToCreate.setDate(new Date());
            this.transferRepository.save(transferToCreate);

            //DESCONTAR VALOR CUENTA ORIGEN
            sourceAccount.setBalance(sourceAccount.getBalance() - transferDTO.getAmount());
            this.accountRepository.save(sourceAccount);

            //AÑADIR EL VALOR EN LA CUENTA OBJETIVO
            targetAccount.setBalance(targetAccount.getBalance() + transferDTO.getAmount());
            this.accountRepository.save(targetAccount);
        } catch (Exception exception) {
            this.msgError = this.msgError == null ? "Error creating new transfer" : this.msgError;
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }
    }

    private void validateTransfer(TransferDTO transferDTO) throws InsertException {
        Optional<AccountEntity> sourceAccountOptional = this.accountRepository
                .findById(transferDTO.getSourceAccountId());
        Optional<AccountEntity> targetAccountOptional = this.accountRepository
                .findById(transferDTO.getTargetAccountId());

        if (!sourceAccountOptional.isPresent()) { //Valido existencia
            this.msgError = "Source account doen't exist";
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }

        if (!targetAccountOptional.isPresent()) {//Valido existencia
            this.msgError = "Target account doen't exist";
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }

        if (sourceAccountOptional.get().equals(targetAccountOptional.get())) { //Valido que no sean las mismas cuentas
            this.msgError = "Source account and target account can't be the same";
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }

        if (transferDTO.getAmount() <= 0) { //Valido monto no negativo
            this.msgError = "Invalid transfer amount";
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }

        if (sourceAccountOptional.get().getBalance() - transferDTO.getAmount() < 0) { //Valido saldo suficiente
            this.msgError = "Insufficient balance";
            throw new InsertException(this.msgError, TransferEntity.class.getName());
        }
    }

    public ArrayList<TransferEntity> getSentTransfersByAccountId(Integer id) throws DocumentNotFoundException {
        try {
            ArrayList<TransferEntity> transfers = this.transferRepository.findBySourceAccountId(id);

            if (!transfers.isEmpty()) {
                return transfers;
            }

            this.msgError = "No transfers sent from this account";
            throw new DocumentNotFoundException(this.msgError, TransferEntity.class.getName());

        } catch (Exception exception) {
            this.msgError = this.msgError == null ? "Error getting account's sent transfers" : this.msgError;
            throw new DocumentNotFoundException(this.msgError, TransferEntity.class.getName());
        }
    }

    public ArrayList<TransferEntity> getReceivedTransfersByAccountId(Integer id) throws DocumentNotFoundException {
        try {
            ArrayList<TransferEntity> transfers = this.transferRepository.findByTargetAccountId(id);

            if (!transfers.isEmpty()) {
                return transfers;
            }

            this.msgError = "No transfers received on this account";
            throw new DocumentNotFoundException(this.msgError, TransferEntity.class.getName());

        } catch (Exception exception) {
            this.msgError = this.msgError == null ? "Error getting account's received transfers" : this.msgError;
            throw new DocumentNotFoundException(this.msgError, TransferEntity.class.getName());
        }
    }
}
