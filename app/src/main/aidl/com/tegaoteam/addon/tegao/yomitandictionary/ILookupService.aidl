// ILookupService.aidl
package com.tegaoteam.addon.tegao.yomitandictionary;

import com.tegaoteam.addon.tegao.yomitandictionary.ILookupCallback;

interface ILookupService {
    void requestLookupResult(in String keyword);
    void registerCallback(ILookupCallback callback);
}