package com.goldeneggs.Pay;

import java.util.List;

/**
 * Interface for payment service.
 */
public interface PayService {

    List<Pay> getAll();
    Pay get(Long id);
    Pay save(Pay pay);
    Pay update(Long id, Pay updatedPay);
    void delete(Long id);
}
