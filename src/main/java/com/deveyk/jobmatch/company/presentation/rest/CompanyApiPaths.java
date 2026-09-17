package com.deveyk.jobmatch.company.presentation.rest;

import com.deveyk.jobmatch.shared.presentation.rest.ApiPaths;


public final class CompanyApiPaths {

    private CompanyApiPaths() {
    }

    public static final class Profile {

        private Profile() {
        }

        public static final String BASE = ApiPaths.BASE_PATH + "/companies";
        public static final String BY_ID = BASE + "/{companyId}";
        public static final String VERIFY = BY_ID + "/verify";

    }

    public static final class Membership {

        private Membership() {
        }

        public static final String BASE = Profile.BY_ID + "/members";
        public static final String BY_USER_ID = BASE + "/{userId}";

    }

}
