package uk.co.huntersix.spring.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse
{
    private String errorCode;
    private String errorMessage;

    public ErrorResponse ()
    {
    }

    public ErrorResponse (final String errorCode, final String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode ()
    {
        return errorCode;
    }

    public void setErrorCode (final String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage ()
    {
        return errorMessage;
    }

    public void setErrorMessage (final String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString ()
    {
        return "ErrorResponse [errCode=" + errorCode + ", errMessage=" + errorMessage
            + "]";
    }
}
