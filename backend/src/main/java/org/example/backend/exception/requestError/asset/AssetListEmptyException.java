package org.example.backend.exception.requestError.asset;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class AssetListEmptyException extends BusinessException {
    public AssetListEmptyException(String message) {
        super(ErrorCode.ASSET_LIST_EMPTY);
    }
}
