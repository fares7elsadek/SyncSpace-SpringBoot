package com.fares7elsadek.syncspace.shared.exceptions;

public class AccessDeniedException extends RuntimeException
{
    public AccessDeniedException(String message)
    {
        super(message);
    }
}
