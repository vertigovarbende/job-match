package com.deveyk.jobmatch.candidate.presentation.rest;

import com.deveyk.jobmatch.shared.presentation.rest.ApiPaths;

public final class CandidateApiPaths {

    private CandidateApiPaths() {
    }

    public static final class Profile {

        private Profile() {
        }

        public static final String BASE = ApiPaths.BASE_PATH + "/candidates/me";

    }

    public static final class Skills {

        private Skills() {
        }

        public static final String BASE = Profile.BASE + "/skills";
        public static final String BY_SKILL_ID = BASE + "/{skillId}";

    }

    public static final class Languages {

        private Languages() {
        }

        public static final String BASE = Profile.BASE + "/languages";
        public static final String BY_LANGUAGE_ID = BASE + "/{languageId}";

    }

    public static final class Experiences {

        private Experiences() {
        }

        public static final String BASE = Profile.BASE + "/experiences";
        public static final String BY_EXPERIENCE_ID = BASE + "/{experienceId}";

    }

    public static final class Educations {

        private Educations() {
        }

        public static final String BASE = Profile.BASE + "/educations";
        public static final String BY_EDUCATION_ID = BASE + "/{educationId}";

    }

    public static final class Certifications {

        private Certifications() {
        }

        public static final String BASE = Profile.BASE + "/certifications";
        public static final String BY_CERTIFICATION_ID = BASE + "/{certificationId}";

    }

}
