package org.example.backend.exception.requestError.asset;

import org.example.backend.exception.ErrorCode;
import org.example.backend.exception.requestError.BusinessException;

public class AssetNotFoundException extends BusinessException {
    public AssetNotFoundException() {
        super(ErrorCode.ASSET_NOT_FOUND);
    }
}
