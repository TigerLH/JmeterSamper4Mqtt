package com.beecloud.jmeter.gui;

import com.beecloud.jmeter.constants.Constants;
import com.beecloud.jmeter.sampler.SubscriberSampler;
import org.apache.jmeter.gui.util.JLabeledRadioI18N;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledPasswordField;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MQTTSubscriberGui extends AbstractSamplerGui implements ChangeListener, ActionListener {

    private static final long serialVersionUID = 240L;

    private final JLabeledTextField brokerUrlField = new JLabeledTextField(Constants.MQTT_PROVIDER_URL);

    private final JLabeledTextField vehicleInfo = new JLabeledTextField(Constants.MQTT_VEHICLE);

    private final JCheckBox retained = new JCheckBox(Constants.MQTT_SEND_AS_RETAINED_MSG, false);

    private final JCheckBox cleanSession = new JCheckBox(Constants.MQTT_CLEAN_SESSION, false);

    private final JLabeledTextField mqttKeepAlive = new JLabeledTextField(Constants.MQTT_KEEP_ALIVE);

    private final JLabeledTextField mqttUser = new JLabeledTextField(Constants.MQTT_USERNAME);

    private final JLabeledTextField mqttPwd = new JLabeledPasswordField(Constants.MQTT_PASSWORD);
    private static final String[] QOS_TYPES_ITEMS = {Constants.MQTT_AT_MOST_ONCE, Constants.MQTT_AT_LEAST_ONCE, Constants.MQTT_EXACTLY_ONCE};
    private final JLabeledRadioI18N typeQoSValue = new JLabeledRadioI18N(Constants.MQTT_QOS, QOS_TYPES_ITEMS, Constants.MQTT_AT_MOST_ONCE);



    public MQTTSubscriberGui() {
        initWindow();
    }

    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getStaticLabel() {
        return Constants.MQTT_SUBSCRIBER_TITLE;
    }


    /**
     * @Descrition:创建新的元素
     * */
    @Override
    public TestElement createTestElement() {
        TestElement sampler = new SubscriberSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    /**
     * @Descrition:配置组装元素信息
     * */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        SubscriberSampler sampler = (SubscriberSampler) el;
        brokerUrlField.setText(sampler.getBrokerUrl());
        vehicleInfo.setText(sampler.getVehicle());
        retained.setSelected(sampler.getRetained());
        cleanSession.setSelected(sampler.getCleanSession());
        mqttKeepAlive.setText(Integer.toString(sampler.getKeepAlive()));
        mqttUser.setText(sampler.getUsername());
        mqttPwd.setText(sampler.getPassword());
    }


    /**
     * @Descrition:GUI上用户输入的值赋值给Sampler
     * */
    @Override
    public void modifyTestElement(TestElement testElement) {
        SubscriberSampler sampler = (SubscriberSampler)testElement;
        super.configureTestElement(sampler);
        sampler.setBrokerUrl(brokerUrlField.getText());
        sampler.setVehicle(vehicleInfo.getText());
        sampler.setUsername(mqttUser.getText());
        sampler.setPassword(mqttPwd.getText());
        sampler.setRetained(retained.isSelected());
        sampler.setCleanSession(cleanSession.isSelected());
        sampler.setQos(Constants.getQos(typeQoSValue.getText()));
        sampler.setKeepAlive(Integer.parseInt(mqttKeepAlive.getText()));
    }



    private void initWindow() {
        setLayout(new BorderLayout());
        brokerUrlField.setText(Constants.MQTT_URL_DEFAULT);
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        JPanel mainPanel = new VerticalPanel();
        add(mainPanel, BorderLayout.CENTER);
        JPanel DPanel = new JPanel();
        DPanel.setLayout(new BoxLayout(DPanel, BoxLayout.X_AXIS));
        DPanel.add(brokerUrlField);
        JPanel ControlPanel = new VerticalPanel();
        ControlPanel.add(DPanel);
        ControlPanel.add(createDestinationPane());
        ControlPanel.add(retained);
        ControlPanel.add(cleanSession);
        ControlPanel.add(createKeepAlivePane());
        ControlPanel.add(createAuthPane());
        ControlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray),
                "连接设置"));
        mainPanel.add(ControlPanel);
        JPanel TPanel = new VerticalPanel();
        TPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Qos Option"));
        typeQoSValue.setLayout(new BoxLayout(typeQoSValue, BoxLayout.X_AXIS));
        TPanel.add(typeQoSValue);
        mainPanel.add(TPanel);
        


        
        
        brokerUrlField.setText(Constants.MQTT_URL_DEFAULT);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "消息类型"));
        mainPanel.add(verticalPanel, BorderLayout.CENTER);
        verticalPanel.setLayout(new GridLayout(0, 1, 0, 0));
        
        JComboBox comboBox_messageType = new JComboBox();
        comboBox_messageType.setFont(new Font("宋体", Font.PLAIN, 12));
        verticalPanel.add(comboBox_messageType);
        
        Component horizontalStrut = Box.createHorizontalStrut(10);
        verticalPanel.add(horizontalStrut);
        
        Component horizontalStrut_1 = Box.createHorizontalStrut(10);
        verticalPanel.add(horizontalStrut_1);
        
        Component horizontalStrut_2 = Box.createHorizontalStrut(10);
        verticalPanel.add(horizontalStrut_2);
    }

    /**
     * 创建用户/密码Panel
     * @return
     */
    private Component createAuthPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(mqttUser);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(mqttPwd);
        panel.add(Box.createHorizontalStrut(10));
        return panel;
    }


    @Override
    public void clearGui() {
        super.clearGui();
    }


    private JPanel createDestinationPane() {
        JPanel panel = new VerticalPanel(); //new BorderLayout(3, 0)
        this.vehicleInfo.setLayout((new BoxLayout(vehicleInfo, BoxLayout.X_AXIS)));
        panel.add(vehicleInfo);
        JPanel TPanel = new JPanel();
        TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
        TPanel.add(Box.createHorizontalStrut(100));
        panel.add(TPanel);
        return panel;
    }


    private JPanel createKeepAlivePane() {
        JPanel panel = new VerticalPanel(); //new BorderLayout(3, 0)
        this.mqttKeepAlive.setLayout((new BoxLayout(mqttKeepAlive, BoxLayout.X_AXIS)));
        panel.add(mqttKeepAlive);
        JPanel TPanel = new JPanel();
        TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
        TPanel.add(Box.createHorizontalStrut(100));
        panel.add(TPanel);
        mqttKeepAlive.setText(Constants.MQTT_KEEP_ALIVE_DEFAULT);
        return panel;
    }

    
    @Override
	public void actionPerformed(ActionEvent e) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void stateChanged(ChangeEvent e) {
     
    }
}
