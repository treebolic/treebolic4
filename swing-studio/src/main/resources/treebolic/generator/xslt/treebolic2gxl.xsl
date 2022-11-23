<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- TREEBOLIC 2 GXL - 15/05/2008 (C) 2004-2008 Author: Bernard Bou -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output method="xml" indent="yes" xmlns:xalan="http://xml.apache.org/xslt" xalan:indent-amount="4" encoding="UTF-8" omit-xml-declaration="no"
		doctype-system="gxl-1.0.dtd" />

	<xsl:template match="/">
		<xsl:element name="gxl">
			<xsl:comment>
				Converted from treebolic to GXL
			</xsl:comment>
			<xsl:element name="graph">
				<xsl:attribute name="id">
                    <xsl:value-of select="generate-id(.)" />
                </xsl:attribute>
				<xsl:attribute name="edgemode">directed</xsl:attribute>
				<xsl:comment>
					SETTINGS
				</xsl:comment>
				<!--TREEBOLIC -->
				<xsl:apply-templates select="//treebolic/@*">
					<xsl:with-param name="context">
						treebolic
					</xsl:with-param>
				</xsl:apply-templates>
				<!--TREE -->
				<xsl:apply-templates select="//tree/@*">
					<xsl:with-param name="context">
						tree
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//tree/img/@*">
					<xsl:with-param name="context">
						tree-img
					</xsl:with-param>
				</xsl:apply-templates>
				<!--NODES -->
				<xsl:apply-templates select="//nodes/@*">
					<xsl:with-param name="context">
						nodes
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//nodes/img/@*">
					<xsl:with-param name="context">
						nodes-img
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//nodes/default.treeedge/@*">
					<xsl:with-param name="context">
						nodes-default-treeedge
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//nodes/default.treeedge/img/@*">
					<xsl:with-param name="context">
						nodes-default-treeedge-img
					</xsl:with-param>
				</xsl:apply-templates>
				<!--EDGES -->
				<xsl:apply-templates select="//edges/@*">
					<xsl:with-param name="context">
						edges
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//edges/default.edge/@*">
					<xsl:with-param name="context">
						edges-default-edge
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="//edges/default.edge/img/@*">
					<xsl:with-param name="context">
						edges-default-edge-img
					</xsl:with-param>
				</xsl:apply-templates>
				<!--DATA -->
				<xsl:comment>
					NODES
				</xsl:comment>
				<xsl:apply-templates select="//*[@id='root']" />
				<xsl:comment>
					EDGES
				</xsl:comment>
				<xsl:apply-templates select="//edges" />
				<!--TOOLS -->
				<xsl:comment>
					TOOLS
				</xsl:comment>
				<xsl:apply-templates select="//tools" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:element name="node">
			<xsl:attribute name="id">
                <xsl:value-of select="./@id" />
            </xsl:attribute>
			<xsl:apply-templates select="./label" />
			<xsl:apply-templates select="./content" />
			<xsl:apply-templates select="./a" />
			<xsl:apply-templates select="./img" />
			<xsl:apply-templates select="./mountpoint" />
			<xsl:apply-templates select="./@*" />
		</xsl:element>
		<xsl:if test="parent::node()/@id!=''">
			<xsl:element name="edge">
				<xsl:attribute name="from">
                    <xsl:value-of select="../@id" />
                </xsl:attribute>
				<xsl:attribute name="to">
                    <xsl:value-of select="./@id" />
                </xsl:attribute>
				<xsl:element name="type">
					<xsl:attribute name="xlink:href">schema.xml#TreeEdge</xsl:attribute>
				</xsl:element>
				<xsl:apply-templates select="./treeedge" />
			</xsl:element>
		</xsl:if>
		<xsl:apply-templates select="./node" />
	</xsl:template>

	<xsl:template match="edges">
		<xsl:comment>
			Non-tree edges
		</xsl:comment>
		<xsl:apply-templates select="./edge" />
	</xsl:template>

	<xsl:template match="edge">
		<xsl:element name="edge">
			<xsl:attribute name="from">
                <xsl:value-of select="./@from" />
            </xsl:attribute>
			<xsl:attribute name="to">
                <xsl:value-of select="./@to" />
            </xsl:attribute>
			<xsl:element name="type">
				<xsl:attribute namespace="http://www.w3.org/1999/xlink" name="xlink:href">schema.xml#NonTreeEdge</xsl:attribute>
			</xsl:element>
			<xsl:apply-templates select="./@*" />
			<xsl:apply-templates select="./label" />
			<xsl:apply-templates select="./img" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="label">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'-label')" />
				</xsl:when>
				<xsl:otherwise>
					label
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="attr">
			<xsl:attribute name="name">
                <xsl:value-of select="$attrname" />
            </xsl:attribute>
			<xsl:element name="string">
				<xsl:value-of select="normalize-space(.)" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="img">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'-img-src')" />
				</xsl:when>
				<xsl:otherwise>
					img-src
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="attr">
			<xsl:attribute name="name">
                <xsl:value-of select="$attrname" />
            </xsl:attribute>
			<xsl:element name="locator">
				<xsl:attribute name="xlink:href">
                    <xsl:value-of select="@src" />
                </xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="content">
		<xsl:element name="attr">
			<xsl:attribute name="name">content</xsl:attribute>
			<xsl:element name="string">
				<xsl:value-of select="normalize-space(.)" />
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="a">
		<xsl:element name="attr">
			<xsl:attribute name="name">link</xsl:attribute>
			<xsl:element name="locator">
				<xsl:attribute name="xlink:href">
                    <xsl:value-of select="@href" />
                </xsl:attribute>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="mountpoint">
		<xsl:element name="attr">
			<xsl:attribute name="name">mountpoint</xsl:attribute>
			<xsl:element name="locator">
				<xsl:attribute name="xlink:href">
                    <xsl:value-of select="./a/@href" />
                </xsl:attribute>
			</xsl:element>
		</xsl:element>
		<xsl:apply-templates select="./@*">
			<xsl:with-param name="context">
				mountpoint
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="treeedge">
		<xsl:apply-templates select="./@*" />
		<xsl:apply-templates select="./label" />
		<xsl:apply-templates select="./img" />
	</xsl:template>

	<!-- MENU -->

	<xsl:template match="tools">
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-label</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="string">
						<xsl:value-of select="./label" />
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-action</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="string">
						<xsl:value-of select="./@action" />
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-match-target</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="string">
						<xsl:value-of select="./@match-target" />
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-match-scope</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="string">
						<xsl:value-of select="./@match-scope" />
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-match-mode</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="string">
						<xsl:value-of select="./@match-mode" />
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
		<xsl:element name="attr">
			<xsl:attribute name="name">menuitem-link</xsl:attribute>
			<xsl:element name="tup">
				<xsl:for-each select=".//menuitem">
					<xsl:element name="locator">
						<xsl:attribute name="xlink:href">
                            <xsl:value-of select="./a/@href" />
                        </xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="@*">
		<xsl:param name="context"/>
		<xsl:variable name="attrname">
			<xsl:choose>
				<xsl:when test="$context != ''">
					<xsl:value-of select="concat($context,'-',name())" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="name()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- <xsl:if test="$context != ''"> <xsl:message>context=<xsl:value-of select="$context"/></xsl:message> </xsl:if> -->
		<xsl:if test="name()!='id'">
			<xsl:element name="attr">
				<xsl:attribute name="name">
                    <xsl:value-of select="$attrname" />
                </xsl:attribute>
                <!-- <xsl:message><xsl:value-of select="concat($context,'#',name())" /></xsl:message> -->
				<xsl:choose>
					<xsl:when test="contains($context,'img') and true()">
						<xsl:element name="locator">
							<xsl:attribute name="xlink:href">
								<xsl:value-of select="." />
							</xsl:attribute>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="string">
							<xsl:value-of select="." />
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

</xsl:stylesheet>
