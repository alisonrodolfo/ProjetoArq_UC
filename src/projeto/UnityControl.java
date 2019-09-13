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
public class UnityControl {

    /**
     * Registradores e memória de dados e instruções(Harvard)*
     */
    private String[] inst_mem; // Memória de instruções
    private int[] data_mem, regs; // Array de registradores e de memoria de dados
    private boolean[] used_regs, used_mem; // Array que indica quais registradores e espaço de memória que estão sendo armazenando algum dado

    ProjetoArq_UC ucp;
    /**
     * Flags usadas por operações de desvios*
     */
    private boolean SinalZ0, SinalP, SinalN; 

    public UnityControl(String[] inst_mem, int[] data_mem, int[] regs, boolean[] used_regs, boolean[] used_mem, ProjetoArq_UC ucp) {
        this.ucp = ucp;
        this.inst_mem = inst_mem;
        this.data_mem = data_mem;
        this.regs = regs;
        this.used_regs = used_regs;
        this.used_mem = used_mem;
    }

    public void STORE(String[] tokens) {

        ucp.escreve("\tSTORE " + tokens[1] + " " + tokens[2]);
        int reg = Integer.parseInt(tokens[1].substring(1));
        if (reg > regs.length - 1 && used_regs[reg] == false) {
            ucp.escreve("\tErro: Registrador inválido");
            return;
        }
        /**
         * Verifica se o parâmetro é um endereço válido (começa com M)*
         */
        if (tokens[2].charAt(0) != 'M') {
            ucp.escreve("\tErro: Endereço inválido");
            return;
        }
        /**
         * Execução da operação
         */
        int dest = Integer.parseInt(tokens[2].substring(1));
        if (dest > data_mem.length - 1) {
            ucp.escreve("\tErro: Endereço fora do limite");
            return;
        }
        data_mem[dest] = regs[reg];
        used_mem[dest] = true;
        ucp.escreve("\tResultado: (MEM)" + tokens[2] + " = " + data_mem[dest]);

    }
    public void LOAD(String[] tokens) {
        ucp.escreve("\tLOAD " + tokens[1] + " " + tokens[2]);
        int val, reg = Integer.parseInt(tokens[1].substring(1));
        if (reg > regs.length - 1) {
            ucp.escreve("\tErro: Registrador inválido");
            return;
        }
        try {
            val = verify_operand(tokens[2]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        /**
         * Execução da operação
         */
        regs[reg] = val;
        used_regs[reg] = true;
        ucp.escreve("\tResultado: " + tokens[1] + " = " + regs[reg]);
    }
    public void ADD(String[] tokens) {
        switch (tokens.length) {
            case 3:
                {//Operação de adição que envolve 1 registradores e um endereço, ou um valor inteiro, ou outro registrador
                    ucp.escreve("\tADD " + tokens[1] + " " + tokens[2]);
                    int reg, op2;
                    try {
                        reg = verify_destiny(tokens[1]);
                        op2 = verify_operand(tokens[2]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[reg] = regs[reg] + op2;
                    set_flags(regs[reg]);
                    ucp.escreve("\tResultado: " + regs[reg]);
                    break;
                }
            case 4:
                {//Operação de adição que envolve 3 registradores, 2 para efetuar a soma e um para armazenar o resultado
                    ucp.escreve("\tADD " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                    int dest = Integer.parseInt(tokens[1].substring(1)), op1, op2;
                    if (dest > regs.length - 1) {
                        ucp.escreve("\tErro: Registrador inválido");
                        return;
                    }       try {
                        op1 = verify_operand(tokens[2]);
                        op2 = verify_operand(tokens[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[dest] = op1 + op2;
                    used_regs[dest] = true;
                    set_flags(regs[dest]);
                    ucp.escreve("\tResultado: " + regs[dest]);
                    break;
                }
            default:
                ucp.escreve("\tErro: Número de argumentos inválido");
                break;
        }
    }
    public void SUB(String[] tokens) {

        switch (tokens.length) {
            case 3:
                {//Operação de subtração que envolve 1 registradores e um endereço, ou um valor inteiro, ou outro registrador
                    ucp.escreve("\tSUB " + tokens[1] + " " + tokens[2]);
                    int reg, op2;
                    try {
                        reg = verify_destiny(tokens[1]);
                        op2 = verify_operand(tokens[2]);
                    } catch (Exception e) {
                        ucp.escreve(e.getMessage());
                        return;
                    }/**
                     * Execução da operação aritmética*
                     */
                    regs[reg] = regs[reg] - op2;
                    set_flags(regs[reg]);
                    ucp.escreve("\tResultado: " + regs[reg]);
                    break;
                }
            case 4:
                {
                    ucp.escreve("\tSUB " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                    int dest = Integer.parseInt(tokens[1].substring(1)), op1, op2;
                    if (dest > regs.length - 1) {
                        ucp.escreve("\tErro: Registrador inválido");
                        return;
                    }       try {
                        op1 = verify_operand(tokens[2]);
                        op2 = verify_operand(tokens[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[dest] = op1 - op2;
                    used_regs[dest] = true;
                    set_flags(regs[dest]);
                    ucp.escreve("\tResultado: " + regs[dest]);
                    break;
                }
            default:
                ucp.escreve("\tErro: Número de argumentos inválido");
                break;
        }
    }
    public void MULT(String[] tokens) {

        switch (tokens.length) {
            case 3:
                {
                    ucp.escreve("\tMULT " + tokens[1] + " " + tokens[2]);
                    int reg, op2;
                    try {
                        reg = verify_destiny(tokens[1]);
                        op2 = verify_operand(tokens[2]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[reg] = regs[reg] * op2;
                    set_flags(regs[reg]);
                    ucp.escreve("\tResultado: " + regs[reg]);
                    break;
                }
            case 4:
                {
                    ucp.escreve("\tMULT " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                    int dest = Integer.parseInt(tokens[1].substring(1)), op1, op2;
                    if (dest > regs.length - 1) {
                        ucp.escreve("\tErro: Registrador inválido");
                        return;
                    }       try {
                        op1 = verify_operand(tokens[2]);
                        op2 = verify_operand(tokens[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[dest] = op1 * op2;
                    used_regs[dest] = true;
                    set_flags(regs[dest]);
                    ucp.escreve("\tResultado: " + regs[dest]);
                    break;
                }
            default:
                ucp.escreve("\tErro: Número de argumentos inválido");
                break;
        }
    }
    public void DIV(String[] tokens) {

        switch (tokens.length) {
            case 3:
                {
                    ucp.escreve("\tDIV " + tokens[1] + " " + tokens[2]);
                    int reg, op2;
                    try {
                        reg = verify_destiny(tokens[1]);
                        op2 = verify_operand(tokens[2]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return;
                    }       if (op2 == 0) {
                        ucp.escreve("\ttErro: Denomidor igual a 0");
                        return;
                    }       /**
                     * Execução da operação aritmética*
                     */
                    regs[reg] = regs[reg] / op2;
                    set_flags(regs[reg]);
                    ucp.escreve("\tResultado: " + regs[reg]);
                    break;
                }
            case 4:
                {
                    ucp.escreve("\tDIV " + tokens[1] + " " + tokens[2] + " " + tokens[3]);
                    int dest = Integer.parseInt(tokens[1].substring(1)), op1, op2;
                    if (dest > regs.length - 1) {
                        ucp.escreve("\tErro: Registrador inválido");
                        return;
                    }       try {
                        op1 = verify_operand(tokens[2]);
                        op2 = verify_operand(tokens[3]);
                    } catch (Exception e) {
                        return;
                    }       if (op2 == 0) {
                        ucp.escreve("\tErro: denomidor igual a 0");
                        return;
                    }       regs[dest] = (op1 / op2);
                    used_regs[dest] = true;
                    set_flags(regs[dest]);
                    ucp.escreve("\tResultado: " + regs[dest]);
                    break;
                }
            default:
                ucp.escreve("\tErro: Número de argumentos inválido");
                break;
        }
    }

    /**
     * Verifica o registrador que armazena o resultado*
     * @param reg_dest
     * @return 
     * @throws java.lang.Exception
     */
    public int verify_destiny(String reg_dest) throws Exception {

        int reg = Integer.parseInt(reg_dest.substring(1));
        /**
         * Verifica se o primeiro argumento(reg) possui algum dado armazenado*
         */
        if (reg > regs.length - 1 && used_regs[reg] == false) {
            throw new Exception("\tErro: Registrador inválido");
        }

        return reg;
    }

    /**
     * Verifica os operandos*
     * @param operand
     * @return 
     * @throws java.lang.Exception
     */
    public int verify_operand(String operand) throws Exception {
        int op;
        /**
         * Verifica se o segundo argumento é um endereço, um registrador ou uma
         * valor fixo*
         */
        switch (operand.charAt(0)) {
            case 'M':
                //Caso seja um endereço na memoria de dados
                op = Integer.parseInt(operand.substring(1)); //Pega o valor do endereço
                if (op < data_mem.length && used_mem[op]) //Verifica se o endereço está dentro da capacidade da memoria e se possui algum dado
                {
                    op = data_mem[op];
                } else {
                    throw new Exception("\tErro: Endereço da memória não utilizado");
                }   break;
            case 'R':
                //Caso seja um registrador
                op = Integer.parseInt(operand.substring(1));
                if (op < regs.length && used_regs[op]) //Verifica se o registrador possui algum valor armazenado
                {
                    op = regs[op];
                } else {
                    throw new Exception("\tErro: Registrador inválido");
                }   break;
            default:
                op = Integer.parseInt(operand); //Caso o segundo parametro seja um valor inteiro
                break;
        }

        return op;

    }

    /**
     * Desvio incondicional*
     * @param tokens
     * @return 
     */
    public int JMP(String[] tokens) {
        if (tokens[1].charAt(0) == 'I')//Verifica se o endereço da instruçao é válido
        {
            if (Integer.parseInt(tokens[1].substring(1)) < inst_mem.length) //Verifica se o endereço da instruçao esta dentro do programa
            {
                return Integer.parseInt(tokens[1].substring(1)); //Retorna próxima instrução
            }
        }
        ucp.escreve("\tNão ocorre desvio");
        return -1; //Não ocorre desvio
    }

    /**
     * Desvios condicionais*
     * @param tokens
     * @return 
     */
    public int JZ(String[] tokens) {
        if (SinalZ0) { //Verifica a flag
            if (tokens[1].charAt(0) == 'I') {
                if (Integer.parseInt(tokens[1].substring(1)) < inst_mem.length) {
                    return Integer.parseInt(tokens[1].substring(1));
                }
            }
        }
        ucp.escreve("\tNão ocorre desvio");
        return -1; 
    }

    public int JNS(String[] tokens) {
        if (SinalN) { //Verifica a flag
            if (tokens[1].charAt(0) == 'I') {
                if (Integer.parseInt(tokens[1].substring(1)) < inst_mem.length) {
                    return Integer.parseInt(tokens[1].substring(1));
                }
            }
        }
        ucp.escreve("\tNão ocorre desvio");
        return -1; 
    }

    public int JS(String[] tokens) {
        if (SinalP) { //Verifica a flag
            if (tokens[1].charAt(0) == 'I') {
                if (Integer.parseInt(tokens[1].substring(1)) < inst_mem.length) {
                    return Integer.parseInt(tokens[1].substring(1));
                }
            }
        }
        ucp.escreve("\tNão ocorre desvio");
        return -1; 
    }

    public void set_flags(int result) {

        if (result == 0) {
            this.SinalZ0 = true;
            this.SinalN = false;
            this.SinalP = false;
        }if (result > 0) {
            this.SinalZ0 = false;
            this.SinalN = false;
            this.SinalP = true;
        }if (result < 0) {
            this.SinalZ0 = false;
            this.SinalN = true;
            this.SinalP = false;
        }
    }

}
