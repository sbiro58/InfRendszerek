package org.biro.szilard.orvos;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public abstract class BaseController {
    
    public BaseController() throws ApplicationException {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        if (!(boolean)servletContext.getAttribute("INIT_STATUS_SUCCESS"))
        {
            throw new ApplicationException();
        }
    }

}