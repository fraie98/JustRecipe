package it.unipi.dii.inginf.lsdb.justrecipe.utils;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void fromListToString() {
        assertEquals("", Utils.fromListToString(new ArrayList<>()));

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("FirstString");
        assertEquals("FirstString", Utils.fromListToString(arrayList));

        arrayList.add("SecondString");
        assertEquals("FirstString, SecondString", Utils.fromListToString(arrayList));

        arrayList.add("ThirdString");
        assertEquals("FirstString, SecondString, ThirdString", Utils.fromListToString(arrayList));

        assertEquals("", Utils.fromListToString(null));
    }

    @Test
    void fromStringToList() {
        String string = "FirstValue";
        assertEquals(new ArrayList<>(Arrays.asList("FirstValue")), Utils.fromStringToList(string));
        string += ",SecondValue";
        assertEquals(new ArrayList<>(Arrays.asList("FirstValue", "SecondValue")), Utils.fromStringToList(string));
        string += ",";
        // The final comma doesn't change the result
        assertEquals(new ArrayList<>(Arrays.asList("FirstValue", "SecondValue")), Utils.fromStringToList(string));

        string = ",";
        assertEquals(new ArrayList<>(), Utils.fromStringToList(string));

        assertEquals(new ArrayList<>(), Utils.fromStringToList(null));
    }

    @Test
    void fromDateToString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse("Mon Jan 04 16:02:37 GMT 2021"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals("2021-01-04", Utils.fromDateToString(cal.getTime()));

        assertEquals(null, Utils.fromDateToString(null));
    }
}