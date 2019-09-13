/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projeto;

/**
 *
 * @author aliso
 */
public class CPU {

    private UnityControl UC;
    ProjetoArq_UC ucp;
    /**
     * Registradores e memória de dados e instruções(Harvard)*
     */
    private String[] inst_mem; // Memória de instruções
    private int[] data_mem, regs; // Array de registradores e de memoria de dados
    private boolean[] used_regs, used_mem; // Array que indica quais registradores e espaço de memória que estão sendo armazenando algum dado

    /**
     * Registradores padrões*
     */
    private int pc, mar;
    private String mbr, ir;
    String aux = "";


    public CPU(int nreg, int[] mem_data, String[] mem_inst, ProjetoArq_UC ucp) {
        this.ucp = ucp;
        /**
         * Numero de registradores*
         */
        regs = new int[nreg];
        used_regs = new boolean[nreg];

        /**
         * Referencia para a memória principal*
         */
        data_mem = mem_data;
        inst_mem = mem_inst;
        used_mem = new boolean[mem_data.length];

        this.pc = 0;

        /**
         * Inicializa os arrays de consulta de uso*
         */
        for (int i = 0; i < regs.length; i++) {
            used_regs[i] = false;
        }

        for (int i = 0; i < data_mem.length; i++) {
            used_mem[i] = false;
        }

        /**
         * Inicializa a unidade de controle (responsável por executar as
         * operações)*
         */
        UC = new UnityControl(inst_mem, data_mem, regs, used_regs, used_mem, ucp);

        
    }

    public void run() {
        String[] tokens;
        int res = 0;
        while (pc < inst_mem.length && inst_mem[pc] != null) {
            fetch();
            tokens = decode();
            res = execute(tokens);
            if (res == 1) {
                break; //Instrução END, encerra o programa
            }
        }
        ucp.escreve("\t======== END ========");
    }

    public void fetch() {
        ucp.escreve("\t======== FETCH ========\n"
                + "\t[MAR] <- [PC]");
        mar = pc;
        mbr = inst_mem[mar];

        ucp.escreve("\t[MAR] = " + "I" + mar + "\n"
                + "\t[MBR] <- [MEM(MAR)]: " + "\n"
                + "\t[MBR] = " + mbr + "\n"
                + "\t[PC] <- PC + 1:" + "\n"
                + "\t[PC] = " + "I" + pc);

        pc++;
        ucp.escreve("\t========================");
    }

    public String[] decode() {
        ucp.escreve("\t======== DECODE ========");

        int i = 1;
        ir = mbr;
        String[] inst;
        inst = ir.split(" ");

        ucp.escreve("\tOperation: " + inst[0]);

        aux = "";
        while (i < inst.length) {
            if (inst[i].charAt(0) == 'R') {
                aux += (inst[i] + " ");
            } else {
                break;
            }
            i++;
        }
        ucp.escreve("\tRegisters: " + aux);

        aux = "";
        while (i < inst.length) {
            if (inst[i].charAt(0) == 'M' || inst[i].charAt(0) == 'I') {
                 aux += (inst[i] + " ");
            } else {
                break;
            }
            i++;
        }
        ucp.escreve("\tAdress: "+aux);

        aux = "";
        while (i < inst.length) {
            aux += (inst[i] + " ");
            i++;
        }
        ucp.escreve("\tNumbers: "+aux);

        ucp.escreve("\t========================");

        return inst;
    }

    public int execute(String[] instruct) {
        ucp.escreve("\t======== EXECUTE ========");
        switch (instruct[0]) {

            case "STORE":
                UC.STORE(instruct);
                break;
            case "LOAD":
                UC.LOAD(instruct);
                break;

            case "ADD":
                UC.ADD(instruct);
                break;

            case "SUB":
                UC.SUB(instruct);
                break;

            case "MULT":
                UC.MULT(instruct);
                break;

            case "DIV":
                UC.DIV(instruct);
                break;

            case "JMP":
                if (UC.JMP(instruct) >= 0) {
                    pc = UC.JMP(instruct);
                    ucp.escreve("\tSalto: Instrução " + pc);
                }
                break;

            case "JZ":
                if (UC.JZ(instruct) >= 0) {
                    pc = UC.JZ(instruct);
                    ucp.escreve("\tSalto: Instrução " + pc);
                }
                break;

            case "JNS":
                if (UC.JNS(instruct) >= 0) {
                    pc = UC.JNS(instruct);
                    ucp.escreve("\tSalto: Instrução " + pc);
                }
                break;

            case "JS":
                if (UC.JS(instruct) >= 0) {
                    pc = UC.JS(instruct);
                    ucp.escreve("\tSalto: Instrução " + pc);
                }
                break;

            case "END":
                return 1;

            default:
                ucp.escreve("\tErro: Operação inválida");
        }

        return 0;
    }

}

