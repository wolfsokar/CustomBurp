package burp;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomBurp {
    public static final int textHeight = new JTextField().getPreferredSize().height;
    private JPanel mainPanel, gridPanel;
    private JToggleButton activatedButton;
    private GridBagConstraints c;
    private JTextField valueTextField;
    private JTextField replaceTextField;
    private JLabel valueLabel;
    private JLabel replaceLabel;
    private JLabel descriptionLabel;
    private IExtensionHelpers helpers;
    private IBurpExtenderCallbacks callbacks;


    public CustomBurp(){
        this.callbacks = BurpExtender.getCallbacks();
        helpers = callbacks.getHelpers();
        createUI();
    }

    private void createUI(){
        mainPanel = new JPanel();
        gridPanel = new JPanel();

        gridPanel.setLayout(new GridBagLayout());
        gridPanel.setPreferredSize(new Dimension(500, textHeight*8));
        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gridPanel.setBorder(BorderFactory.createTitledBorder("My Extension"));

        c = new GridBagConstraints();

        valueTextField = new JTextField();
        replaceTextField = new JTextField();

        valueTextField.setPreferredSize(new Dimension(250, textHeight));
        replaceTextField.setPreferredSize(new Dimension(250, textHeight));

        descriptionLabel = new JLabel("Specify the parameter and the new value for it.");
        valueLabel = new JLabel("Parameter: ");
        replaceLabel = new JLabel("New Value: ");

        activatedButton = new JToggleButton("Activate");
        activatedButton.addChangeListener(e -> {
            if (activatedButton.isSelected()) {
                activatedButton.setText("Deactivate");
            } else {
                activatedButton.setText("Activate");
            }
        });

        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 1;
        gridPanel.add(valueLabel, c);
        c.gridy = 2;
        gridPanel.add(replaceLabel, c);

        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        gridPanel.add(valueTextField, c);
        c.gridy = 2;
        gridPanel.add(replaceTextField, c);
        c.gridy = 3;
        gridPanel.add(activatedButton, c);

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        gridPanel.add(descriptionLabel, c);

        Dimension buttonDimension = new Dimension(200, new JTextField().getPreferredSize().height);
        activatedButton.setPreferredSize(buttonDimension);
        activatedButton.setMaximumSize(buttonDimension);
        activatedButton.setMinimumSize(buttonDimension);

        mainPanel.add(gridPanel);
    }

    public JPanel getUI() {
        return mainPanel;
    }

    public void modifyRequest(IHttpRequestResponse messageInfo) {
        //Extract the request
        IRequestInfo request = helpers.analyzeRequest(messageInfo);
        //Check if the extension is enabled
        if(activatedButton.isSelected()){
            //Get every parameter in the request and compare it with the one we entered
            for(IParameter parameter: request.getParameters()){
                String regex = ""+valueTextField.getText();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(parameter.getName());
                //If the parameter is found, then replace it with the new value
                if(matcher.find()){
                    messageInfo.setRequest(helpers.updateParameter(
                            messageInfo.getRequest(),
                            helpers.buildParameter(
                                    parameter.getName(),
                                    replaceTextField.getText(),
                                    parameter.getType()
                            )
                        )
                    );
                }
            }
        }
    }
}
