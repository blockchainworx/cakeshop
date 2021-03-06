package com.jpmorgan.cakeshop.service.impl;

import com.jpmorgan.cakeshop.dao.ContractDAO;
import com.jpmorgan.cakeshop.error.APIException;
import com.jpmorgan.cakeshop.model.Contract;
import com.jpmorgan.cakeshop.model.ContractABI;
import com.jpmorgan.cakeshop.model.TransactionResult;
import com.jpmorgan.cakeshop.service.ContractRegistryService;
import com.jpmorgan.cakeshop.service.ContractService;
import com.jpmorgan.cakeshop.service.ContractService.CodeType;
import com.jpmorgan.cakeshop.service.TransactionService;
import com.jpmorgan.cakeshop.util.FileUtils;
import com.jpmorgan.cakeshop.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class ContractRegistryServiceImpl implements ContractRegistryService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ContractRegistryServiceImpl.class);

    private static final String REGISTRY_ABI_FILE =
            "contracts" + File.separator + "ContractRegistry.abi.json";

    @Value("${cakeshop.config.dir}")
    private String CONFIG_ROOT;

    @Autowired
    private ContractService contractService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ContractDAO contractDAO;

    public ContractRegistryServiceImpl() throws IOException {
    }

    @Override
    public TransactionResult register(String from, String id, String name, String abi, String code,
        CodeType codeType, Long createdDate, String privateFor) throws APIException {

        LOG.info("Registering contract {} with address {}", name, id);

        Contract contract = new Contract(id, name, abi, code, codeType, null, createdDate,
            privateFor);
        try {
            contractDAO.save(contract);
        } catch (IOException e) {
            throw new APIException("error saving private contract to database", e);
        }
        return null;
    }

    @Override
    public Contract getById(String id) throws APIException {
        try {
            Contract contract = contractDAO.getById(id);
            return contract;
        } catch (IOException e) {
            throw new APIException("Error reading private contract from database", e);
        }
    }

    @Override
    public Contract getByName(String name) throws APIException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Contract> list() throws APIException {

        List<Contract> contracts = contractDAO.list();
        for (Contract contract : contracts) {
            if (StringUtils.isNotBlank(contract.getPrivateFor())) {
                try {
                    // will not succeed if this node is not in privateFor, mark for front end
                    contractService.get(contract.getAddress());
                } catch (APIException e) {
                    LOG.info("Contract {} is private, marking as such", contract.getAddress());
                    contract.setPrivateFor("private");
                }
            }
        }

        contracts.sort(Comparator.comparing(Contract::getCreatedDate));

        return contracts;
    }

    @Override
    public List<Contract> listByOwner(String owner) throws APIException {
        // TODO Auto-generated method stub
        return null;
    }
}
