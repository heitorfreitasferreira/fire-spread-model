//package model.vento;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MatrizVentoTest {
//
//    @org.junit.jupiter.api.Test
//    void getInstanceReturnsInstanceOfMatrizVento() {
//        MatrizVento matrizVento = MatrizVento.getInstance(1,0.16,0.3,DirecoesVento.S);
//        assertNotNull(matrizVento);
//
//    }
//    @org.junit.jupiter.api.Test
//    void DecaiZeroEverybodyEquals() {
//        var m = MatrizVento.getInstance(1,1,0,DirecoesVento.S).getMatrizVento();
//        assertEquals(m[0][0],m[0][1]);
//        assertEquals(m[0][0],m[0][2]);
//        assertEquals(m[0][0],m[1][0]);
//        assertEquals(m[0][0],m[1][2]);
//        assertEquals(m[0][0],m[2][0]);
//        assertEquals(m[0][0],m[2][1]);
//        assertEquals(m[0][0],m[2][2]);
//    }
//    @org.junit.jupiter.api.Test
//    void ventoSentidoOpostos() {
//        var m1 = MatrizVento.getInstance(1,0.16,0.3,DirecoesVento.S).getMatrizVento();
//        var m2 = MatrizVento.getInstance(1,0.16,0.3,DirecoesVento.N).getMatrizVento();
//        assertEquals(m1[0][1],m2[2][1]);
//    }
//
//    @org.junit.jupiter.api.Test
//    void firstGetInstanceThrowsIllegalstateException() {
//        assertThrows(IllegalStateException.class, () -> MatrizVento.getInstance());
//    }
//
//    @org.junit.jupiter.api.Test
//    void secondGetInstanceWithoutArgsGetsTheSingleton() {
//        var m1 = MatrizVento.getInstance(1,0.16,0.3,DirecoesVento.S);
//        var m2 = MatrizVento.getInstance();
//        assertEquals(m1,m2);
//    }
//    @org.junit.jupiter.api.Test
//    void directionNamesMatch() {
//        assertEquals("n",DirecoesVento.N.toString());
//        assertEquals("ne",DirecoesVento.NE.toString());
//        assertEquals("e",DirecoesVento.E.toString());
//        assertEquals("se",DirecoesVento.SE.toString());
//        assertEquals("s",DirecoesVento.S.toString());
//        assertEquals("so",DirecoesVento.SO.toString());
//        assertEquals("o",DirecoesVento.O.toString());
//        assertEquals("no",DirecoesVento.NO.toString());
//    }
//}