package com.example.TCBA.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StackHolderProfileResponse {

    private String stackHolderId;
    private String firstName;
    private String lastName;
    private String gst;
    private String license;
    private String email;
    private String phoneNumber;
}
