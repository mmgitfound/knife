package U2C;

import java.awt.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import burp.Getter;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IMessageEditorController;
import burp.IMessageEditorTab;
import burp.IMessageEditorTabFactory;
import burp.IRequestInfo;
import burp.IResponseInfo;
import burp.ITextEditor;

public class JSONBeautifier implements IMessageEditorTab,IMessageEditorTabFactory
{
    private ITextEditor txtInput;
    private byte[] originContent;
    private IExtensionHelpers helpers;
    public JSONBeautifier(IMessageEditorController controller, boolean editable, IExtensionHelpers helpers, IBurpExtenderCallbacks callbacks)
    {
        txtInput = callbacks.createTextEditor();
        txtInput.setEditable(editable);
        this.helpers = helpers;
    }

    @Override
    public String getTabCaption()
    {
        return "JSON";
    }

    @Override
    public Component getUiComponent()
    {
        return txtInput.getComponent();
    }

    @Override
    public boolean isEnabled(byte[] content, boolean isRequest)
    {   
    	originContent = content;
        if (isRequest) {
        	IRequestInfo requestInfo = helpers.analyzeRequest(content);
            return requestInfo.getContentType() == IRequestInfo.CONTENT_TYPE_JSON;
        } else {
        	IResponseInfo responseInfo = helpers.analyzeResponse(content);
            return responseInfo.getInferredMimeType().equals("JSON");
        }
    	
    }

    @Override
    public void setMessage(byte[] content, boolean isRequest)
    {    	
    	 String json = "";
         if (content == null) {
             // clear our display
             txtInput.setText("none".getBytes());
             txtInput.setEditable(false);
         } else {
             //Take the input, determine request/response, parse as json, then print prettily.
             Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
             //Get only the JSON part of the content
             byte[] body = new Getter().getBody(isRequest, content);
             try {
                 JsonParser jp = new JsonParser();
                 JsonElement je = jp.parse(new String(body));
                 json = gson.toJson(je);
                 txtInput.setText(json.getBytes());
             } catch (Exception e) {
                 txtInput.setText(e.toString().getBytes());
             }

         }
    }

    @Override
    public byte[] getMessage()
    {
    	//byte[] text = txtInput.getText();
        //return text;
    	return originContent;
    	//change the return value of getMessage() method to the origin content to tell burp don't change the original response

    }

    @Override
    public boolean isModified()
    {
        //return txtInput.isTextModified();
        return false;
        //change the return value of isModified() method to false. to let burp don't change the original response) 
    }

    @Override
    public byte[] getSelectedData()
    {
        return txtInput.getSelectedText();
    }
    
    
    public static void main(String args[]) {
		System.out.print("");
	}

	@Override
	public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
		// TODO Auto-generated method stub
		return this;
	}
}