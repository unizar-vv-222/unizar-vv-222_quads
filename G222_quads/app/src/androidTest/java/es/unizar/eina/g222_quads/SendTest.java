package es.unizar.eina.g222_quads;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import es.unizar.eina.g222_quads.ui.quads.G222_quads;
import es.unizar.eina.send.SendAbstractionImpl;

import es.unizar.eina.send.WhatsAppImplementor;
import es.unizar.eina.send.SMSImplementor;



@RunWith(AndroidJUnit4.class)
public class SendTest {
    @Rule
    public ActivityScenarioRule<G222_quads> scenarioRule = new ActivityScenarioRule<>(G222_quads.class);

    /*
     *  Verificar que, cuando se indica el método "whatsapp", se selecciona correctamente la
     *  implementación WhatsAppImplementor
     */
    @Test
    public void testWhatsAppImplementorSelection() throws Exception {
        scenarioRule.getScenario().onActivity(activity -> {
            SendAbstractionImpl send = new SendAbstractionImpl(activity, "whatsapp");

            try {
                java.lang.reflect.Field field = SendAbstractionImpl.class.getDeclaredField("implementor");
                field.setAccessible(true);
                Object impl = field.get(send);

                assertEquals(WhatsAppImplementor.class, impl.getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /*
     * Verificar que, cuando el método no es "whatsapp", se selecciona la implementación
     * por defecto SMSImplementor.
     */
    @Test
    public void testSMSImplementorSelection() {
        scenarioRule.getScenario().onActivity(activity -> {
            SendAbstractionImpl send = new SendAbstractionImpl(activity, "sms");

            try {
                java.lang.reflect.Field field = SendAbstractionImpl.class.getDeclaredField("implementor");
                field.setAccessible(true);
                Object impl = field.get(send);

                assertEquals(SMSImplementor.class, impl.getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Clase auxliar para la ejecución del último test
    class FakeImplementor implements es.unizar.eina.send.SendImplementor {
        public boolean called = false;
        public String phone;
        public String message;

        public void setSourceActivity(android.app.Activity source) {}
        public android.app.Activity getSourceActivity() { return null; }

        public void send(String phone, String message) {
            called = true;
            this.phone = phone;
            this.message = message;
        }
    }

    /*
    * Verificar que, cuando el método no es "whatsapp", se selecciona la implementación
    * por defecto SMSImplementor.
    */
    @Test
    public void testSendDelegation() {
        scenarioRule.getScenario().onActivity(activity -> {
            SendAbstractionImpl send = new SendAbstractionImpl(activity, "sms");

            FakeImplementor fake = new FakeImplementor();

            try {
                java.lang.reflect.Field field = SendAbstractionImpl.class.getDeclaredField("implementor");
                field.setAccessible(true);
                field.set(send, fake);

                send.send("123456789", "mensaje prueba");

                assertEquals(true, fake.called);
                assertEquals("123456789", fake.phone);
                assertEquals("mensaje prueba", fake.message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
