package com.apperian.api;

import com.apperian.api.application.Applications;
import com.apperian.api.publishing.Publishing;
import com.apperian.api.signing.Signing;
import com.apperian.api.users.Users;

public class ApperianEase {
    public static final Users USERS = new Users();
    public static final Applications APPLICATIONS = new Applications();
    public static final Signing SIGNING = new Signing();
    public static final Publishing PUBLISHING = new Publishing();
}
