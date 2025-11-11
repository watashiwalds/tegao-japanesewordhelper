// IRecognitionService.aidl
package com.tegaoteam.addon.tegao.handwritingrecognition;

import java.util.List;

interface IRecognitionService {
    List<String> requestInputSuggestions(in byte[] input);
}