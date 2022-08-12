package com.limo.limec.parser;

public class Exception {

    public static class ParsingException extends Throwable {
        public String details;

        public ParsingException(String details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return details;
        }
    }
}
