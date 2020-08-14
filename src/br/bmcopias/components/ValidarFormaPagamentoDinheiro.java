package br.bmcopias.components;

import br.bmcopias.util.Util;

public class ValidarFormaPagamentoDinheiro extends FormaPagamentoValidation{

	public ValidarFormaPagamentoDinheiro(SelecaoFormaPagamentoFiltro fltr) {
		super(fltr);
		// TODO Auto-generated constructor stub
	}

	public boolean validate() {
		mensagensErro   = new StringBuilder();
		boolean retorno = true;
		
		if(filtro.VLR_ORIG_PARCELA_TXT.getValue() == null){
			mensagensErro.append("� necess�rio informar o valor. \n");
			retorno = false;
		}
		
		if(Util.getAsDouble(filtro.VLR_ORIG_PARCELA_TXT.getText()) == 0){
			mensagensErro.append("� necess�rio informar o valor. \n");
			retorno = false;
		}		
		
		if(Util.isBlankOrNull(filtro.DATA_VCTO_ORIG_PARCELA_DATE_LBL.getText())){
			mensagensErro.append("� necess�rio preencher a data de vencimento. ");
			retorno = false;
		}
		
		return retorno;
	}

}
