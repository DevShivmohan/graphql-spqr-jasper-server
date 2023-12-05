package com.jasperReportGenerator.endpoint;

import com.jasperReportGenerator.config.JasperServerConfigProperties;
import com.jasperReportGenerator.constants.ApiConstants;
import com.jasperReportGenerator.dto.JasperReportDto;
import com.jasperReportGenerator.service.DownloadReportService;
import com.jasperReportGenerator.service.JasperReportApiCallService;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@GraphQLApi
@Service
@AllArgsConstructor
public class JasperReportApiQueryResolver {
    private final JasperServerConfigProperties jasperServerConfigProperties;
    private final DownloadReportService downloadReportService;
    private final JasperReportApiCallService jasperReportApiCallService;

    /**
     * To fetch the report from jasper server
     *
     * @param jasperReportDto
     * @return
     * @throws Throwable
     */
    @GraphQLQuery(name = "fetchReportFromReportServer")
    public String fetchReportFromReportServer(JasperReportDto jasperReportDto) throws Throwable {
        final StringBuilder baseURL = new StringBuilder(jasperServerConfigProperties.getUrl());
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL.append(jasperReportDto.getReportUri()).append(ApiConstants.DOT).append(jasperReportDto.getFileFormat()).toString())
                .queryParam(ApiConstants.REPORT_LOCALE, (jasperReportDto.getLocaleName() == null || jasperReportDto.getLocaleName().isBlank() ? jasperServerConfigProperties.getDefaultLocale() : jasperReportDto.getLocaleName()));
        final var inputStream= jasperReportApiCallService.callJasperReportApi(uriComponentsBuilder.toUriString());
        return downloadReportService.downloadReportIntoFileSystem(inputStream, jasperReportDto.getReportUri(),jasperReportDto.getFileFormat()).getAbsolutePath();
    }

}
